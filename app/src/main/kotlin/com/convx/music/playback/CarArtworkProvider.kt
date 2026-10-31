/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.playback

import android.content.ContentProvider
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.ParcelFileDescriptor
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import coil3.size.Size
import coil3.toBitmap
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.security.MessageDigest

/**
 * Serves browse-tree artwork to car hosts. Android Auto only loads `content://` and
 * `android.resource://` icons, so remote and MediaStore artwork is re-served from a
 * local file cache. The provider is not exported: [grantReadAccess] gives a connected
 * car host read access to this authority only.
 */
class CarArtworkProvider : ContentProvider() {

    override fun onCreate() = true

    override fun getType(uri: Uri) = ARTWORK_MIME_TYPE

    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor {
        if (mode != "r") throw FileNotFoundException("Artwork is read-only")
        val context = context ?: throw FileNotFoundException(uri.toString())
        val source = uri.getQueryParameter(SOURCE_PARAMETER)?.let(Uri::parse)
            ?.takeIf(::isSupportedSource)
            ?: throw FileNotFoundException(uri.toString())

        val file = cachedArtwork(context, source) ?: throw FileNotFoundException(uri.toString())
        return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
    }

    private fun cachedArtwork(context: Context, source: Uri): File? {
        val directory = File(context.cacheDir, CACHE_DIRECTORY).apply { mkdirs() }
        val file = File(directory, cacheFileName(source))
        if (file.isFile && file.length() > 0) {
            file.setLastModified(System.currentTimeMillis())
            return file
        }

        // ContentProvider.openFile is a blocking binder call by contract.
        val bitmap = runBlocking { loadBitmap(context, source) } ?: return null
        // Each request writes its own temp file, so concurrent requests for one image never mix bytes.
        var partialFile: File? = null
        return try {
            val tempFile = File.createTempFile(file.name, PARTIAL_SUFFIX, directory).also { partialFile = it }
            tempFile.outputStream().use { output ->
                if (!bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, output)) return null
            }
            if (!tempFile.renameTo(file)) return null
            partialFile = null
            pruneCache(directory)
            file
        } catch (e: IOException) {
            Timber.tag(TAG).w(e, "Failed to cache car artwork")
            null
        } finally {
            partialFile?.delete()
        }
    }

    private suspend fun loadBitmap(context: Context, source: Uri): Bitmap? {
        val request = ImageRequest.Builder(context)
            .data(source.toString())
            .size(Size(ARTWORK_SIZE_PX, ARTWORK_SIZE_PX))
            .allowHardware(false)
            .build()
        return (context.imageLoader.execute(request) as? SuccessResult)?.image?.toBitmap()
    }

    private fun pruneCache(directory: File) {
        val files = directory.listFiles { file -> file.isFile && !file.name.endsWith(PARTIAL_SUFFIX) }
            ?: return
        if (files.size <= MAX_CACHED_FILES) return
        files.sortedBy(File::lastModified)
            .take(files.size - MAX_CACHED_FILES)
            .forEach(File::delete)
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?,
    ): Cursor? = null

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?,
    ) = 0

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?) = 0

    companion object {
        private const val TAG = "CarArtworkProvider"
        private const val AUTHORITY_SUFFIX = ".car.artwork"
        private const val SOURCE_PARAMETER = "src"
        private const val ARTWORK_MIME_TYPE = "image/jpeg"
        private const val CACHE_DIRECTORY = "car_artwork"
        private const val PARTIAL_SUFFIX = ".partial"
        private const val ARTWORK_SIZE_PX = 512
        private const val JPEG_QUALITY = 90
        private const val MAX_CACHED_FILES = 400
        private const val MEDIA_STORE_AUTHORITY = "media"

        private fun authority(context: Context) = context.packageName + AUTHORITY_SUFFIX

        /** Returns a car-loadable URI for [source], or [source] when it is already loadable. */
        fun artworkUri(context: Context, source: Uri?): Uri? = when {
            source == null -> null
            source.scheme == ContentResolver.SCHEME_ANDROID_RESOURCE -> source
            !isSupportedSource(source) -> null
            else -> Uri.Builder()
                .scheme(ContentResolver.SCHEME_CONTENT)
                .authority(authority(context))
                .appendPath("art")
                .appendQueryParameter(SOURCE_PARAMETER, source.toString())
                .build()
        }

        /** Lets [packageName] read every artwork URI served by this provider. */
        fun grantReadAccess(context: Context, packageName: String) {
            if (packageName == context.packageName) return
            val rootUri = Uri.Builder()
                .scheme(ContentResolver.SCHEME_CONTENT)
                .authority(authority(context))
                .build()
            try {
                context.grantUriPermission(
                    packageName,
                    rootUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PREFIX_URI_PERMISSION,
                )
            } catch (e: SecurityException) {
                Timber.tag(TAG).w(e, "Could not grant artwork access to $packageName")
            }
        }

        private fun isSupportedSource(source: Uri) = when (source.scheme) {
            "https", "http" -> !source.host.isNullOrBlank()
            ContentResolver.SCHEME_CONTENT -> source.authority == MEDIA_STORE_AUTHORITY
            else -> false
        }

        private fun cacheFileName(source: Uri): String =
            MessageDigest.getInstance("SHA-256")
                .digest(source.toString().toByteArray())
                .joinToString("") { "%02x".format(it) }
    }
}
