/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.io.InputStream
import kotlin.math.max

/**
 * The single gate every user-supplied image goes through — player icons, DIY stickers,
 * wallpapers, preset thumbnails, and anything unpacked out of an imported preset archive.
 *
 * Nothing downstream ever sees the original bytes. Everything is re-decoded, downsampled to the
 * ceiling its use actually needs, stripped of metadata, and re-encoded to WebP inside app
 * storage. That is what makes an imported preset from a stranger safe to open: a hostile file
 * either fails to decode here or comes out the other side as an ordinary bitmap.
 */
object MediaImport {

    /** What the image is going to be used for. Drives the resolution ceiling and encoding. */
    enum class Kind(
        val maxDim: Int,
        val lossless: Boolean,
        val quality: Int,
    ) {
        /** Play/pause/next/… glyphs. Rendered at ≤64dp, so 512 covers 4x density and then some. */
        PLAYER_ICON(512, lossless = true, quality = 100),

        /** Slider thumbs. Tiny on screen; 256 is already generous. */
        SEEK_THUMB(256, lossless = true, quality = 100),

        /** DIY stickers. Can be blown up to fill a chunk of the screen, so they get real headroom. */
        STICKER(2048, lossless = false, quality = 92),

        /** Home/player backgrounds. */
        WALLPAPER(2560, lossless = false, quality = 85),

        /** Preset grid thumbnails. Never shown larger than a card. */
        THUMBNAIL(512, lossless = false, quality = 80),
    }

    enum class Error {
        TOO_LARGE,
        UNSUPPORTED_TYPE,
        UNSAFE_SVG,
        CORRUPT,
        TOO_MANY_PIXELS,
        IO,
    }

    sealed interface Result {
        /** [file] lives inside app storage and is safe to read back without further checks. */
        data class Ok(val file: File, val width: Int, val height: Int, val isVector: Boolean) : Result
        data class Failed(val error: Error) : Result
    }

    /** Hard ceiling on the source file itself, before we ever ask a decoder to look at it. */
    private const val MAX_SOURCE_BYTES = 12L * 1024 * 1024

    /** SVGs are text and stay text; they never need to be anywhere near this big. */
    private const val MAX_SVG_BYTES = 1L * 1024 * 1024

    /**
     * Refuse anything whose decoded pixel count would be absurd. A 200 KB PNG can legally declare
     * 30000x30000 and expand to gigabytes in RAM — this is the decompression-bomb guard, and it
     * runs on the header before a single pixel is allocated.
     */
    private const val MAX_SOURCE_PIXELS = 50_000_000L

    private val RASTER_TYPES = setOf("image/png", "image/jpeg", "image/jpg", "image/webp")
    private const val SVG_TYPE = "image/svg+xml"

    /**
     * Validates [uri] and writes a normalised copy into [destDir]/[baseName] (extension chosen
     * here). Any existing file at that name is replaced.
     *
     * Safe to call on a background dispatcher only — it does real IO and decoding.
     */
    fun import(
        context: Context,
        uri: Uri,
        kind: Kind,
        destDir: File,
        baseName: String,
        allowVector: Boolean = true,
    ): Result {
        val mime = resolvedMimeType(context, uri)
        val size = sourceSize(context, uri)

        if (mime == SVG_TYPE) {
            if (!allowVector) return Result.Failed(Error.UNSUPPORTED_TYPE)
            if (size != null && size > MAX_SVG_BYTES) return Result.Failed(Error.TOO_LARGE)
            return importSvg(context, uri, destDir, baseName)
        }
        if (mime !in RASTER_TYPES) return Result.Failed(Error.UNSUPPORTED_TYPE)
        if (size != null && size > MAX_SOURCE_BYTES) return Result.Failed(Error.TOO_LARGE)

        return importRaster(context, uri, kind, destDir, baseName)
    }

    private fun importRaster(
        context: Context,
        uri: Uri,
        kind: Kind,
        destDir: File,
        baseName: String,
    ): Result {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        try {
            // decodeStream always returns null under inJustDecodeBounds — it reports through
            // `bounds`, not a return value. Only the *stream* being absent is an IO failure here.
            val stream = open(context, uri) ?: return Result.Failed(Error.IO)
            stream.use { BitmapFactory.decodeStream(it, null, bounds) }
        } catch (_: Exception) {
            return Result.Failed(Error.CORRUPT)
        }
        val srcW = bounds.outWidth
        val srcH = bounds.outHeight
        if (srcW <= 0 || srcH <= 0) return Result.Failed(Error.CORRUPT)
        if (srcW.toLong() * srcH > MAX_SOURCE_PIXELS) return Result.Failed(Error.TOO_MANY_PIXELS)

        // Cheap power-of-two downsample first so the full-size bitmap is never allocated.
        val opts = BitmapFactory.Options().apply {
            inSampleSize = sampleSizeFor(max(srcW, srcH), kind.maxDim)
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }
        val decoded = try {
            open(context, uri)?.use { BitmapFactory.decodeStream(it, null, opts) }
        } catch (_: OutOfMemoryError) {
            return Result.Failed(Error.TOO_MANY_PIXELS)
        } catch (_: Exception) {
            return Result.Failed(Error.CORRUPT)
        } ?: return Result.Failed(Error.CORRUPT)

        val oriented = applyExifRotation(context, uri, decoded)
        val scaled = scaleToFit(oriented, kind.maxDim)

        val out = File(destDir, "$baseName.webp")
        return try {
            destDir.mkdirs()
            out.outputStream().use { stream ->
                scaled.compress(webpFormat(kind.lossless), kind.quality, stream)
            }
            // Re-encoding through Bitmap drops every EXIF/XMP/ICC chunk the source carried,
            // which is how location data in a user's photo stops travelling with a shared preset.
            Result.Ok(out, scaled.width, scaled.height, isVector = false)
        } catch (_: Exception) {
            out.delete()
            Result.Failed(Error.IO)
        } finally {
            if (scaled !== oriented) oriented.recycle()
            if (oriented !== decoded) decoded.recycle()
        }
    }

    /**
     * SVGs are copied through as text, so they get read rather than decoded — which means the
     * usual XML attack surface applies. Anything that could pull in an external resource or
     * expand exponentially is rejected outright rather than sanitised; a rewritten SVG is a
     * subtly different drawing, and silently changing a user's artwork is worse than refusing it.
     */
    private fun importSvg(context: Context, uri: Uri, destDir: File, baseName: String): Result {
        val text = try {
            open(context, uri)?.use { it.readBytes() }?.toString(Charsets.UTF_8)
                ?: return Result.Failed(Error.IO)
        } catch (_: Exception) {
            return Result.Failed(Error.IO)
        }
        if (text.length > MAX_SVG_BYTES) return Result.Failed(Error.TOO_LARGE)
        if (!isSafeSvg(text)) return Result.Failed(Error.UNSAFE_SVG)

        val out = File(destDir, "$baseName.svg")
        return try {
            destDir.mkdirs()
            out.writeText(text)
            Result.Ok(out, width = 0, height = 0, isVector = true)
        } catch (_: Exception) {
            out.delete()
            Result.Failed(Error.IO)
        }
    }

    /** @return false if the document uses any construct we refuse to render. */
    fun isSafeSvg(text: String): Boolean {
        val lower = text.lowercase()
        if (!lower.contains("<svg")) return false
        // Entity expansion (billion laughs) and external DTD fetches.
        if (lower.contains("<!doctype") || lower.contains("<!entity")) return false
        // Active content.
        if (lower.contains("<script") || lower.contains("<foreignobject")) return false
        if (Regex("""\son\w+\s*=""").containsMatchIn(lower)) return false
        if (lower.contains("javascript:")) return false
        // Anything that reaches off-device or off-file for content.
        if (lower.contains("<use") && lower.contains("http")) return false
        if (Regex("""<image[^>]*(href|src)\s*=""").containsMatchIn(lower)) return false
        if (lower.contains("xlink:href=\"http") || lower.contains("href=\"http")) return false
        return true
    }

    /**
     * The picked file's type.
     *
     * Providers are inconsistent: plenty report `application/octet-stream`, or nothing at all, for
     * a perfectly ordinary PNG — and SVGs routinely come back as some flavour of XML. Falling back
     * to the file name keeps those picks working. This only decides which *decoder* to try; the
     * bytes still have to survive that decoder, so a mislabelled file fails there rather than here.
     */
    private fun resolvedMimeType(context: Context, uri: Uri): String? {
        val declared = context.contentResolver.getType(uri)
            ?.lowercase()?.substringBefore(';')?.trim()
        if (declared != null && (declared in RASTER_TYPES || declared == SVG_TYPE)) return declared

        val name = displayName(context, uri) ?: uri.lastPathSegment.orEmpty()
        return when (name.substringAfterLast('.', "").lowercase()) {
            "png" -> "image/png"
            "jpg", "jpeg" -> "image/jpeg"
            "webp" -> "image/webp"
            "svg" -> SVG_TYPE
            else -> declared
        }
    }

    private fun displayName(context: Context, uri: Uri): String? = runCatching {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && index >= 0) cursor.getString(index) else null
        }
    }.getOrNull()

    private fun open(context: Context, uri: Uri): InputStream? =
        runCatching { context.contentResolver.openInputStream(uri) }.getOrNull()

    private fun sourceSize(context: Context, uri: Uri): Long? = runCatching {
        context.contentResolver.openAssetFileDescriptor(uri, "r")?.use { it.length }
            ?.takeIf { it >= 0 }
    }.getOrNull()

    private fun sampleSizeFor(sourceMax: Int, targetMax: Int): Int {
        var sample = 1
        while (sourceMax / (sample * 2) >= targetMax) sample *= 2
        return sample
    }

    private fun scaleToFit(src: Bitmap, maxDim: Int): Bitmap {
        val longest = max(src.width, src.height)
        if (longest <= maxDim) return src
        val ratio = maxDim.toFloat() / longest
        return Bitmap.createScaledBitmap(
            src,
            (src.width * ratio).toInt().coerceAtLeast(1),
            (src.height * ratio).toInt().coerceAtLeast(1),
            true,
        )
    }

    private fun applyExifRotation(context: Context, uri: Uri, bitmap: Bitmap): Bitmap {
        val orientation = runCatching {
            open(context, uri)?.use { ExifInterface(it).getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL,
            ) }
        }.getOrNull() ?: return bitmap

        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
            else -> return bitmap
        }
        return runCatching {
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }.getOrDefault(bitmap)
    }

    @Suppress("DEPRECATION")
    private fun webpFormat(lossless: Boolean): Bitmap.CompressFormat =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (lossless) Bitmap.CompressFormat.WEBP_LOSSLESS else Bitmap.CompressFormat.WEBP_LOSSY
        } else {
            Bitmap.CompressFormat.WEBP
        }
}
