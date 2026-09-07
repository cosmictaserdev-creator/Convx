/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.utils.preset

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.convxy.music.constants.CustomFontPathKey
import com.convxy.music.constants.DiyLayoutKey
import com.convxy.music.constants.HomeBackgroundPathKey
import com.convxy.music.constants.PlayerIconsKey
import com.convxy.music.constants.V2PlayerIconsKey
import com.convxy.music.constants.PrefType
import com.convxy.music.constants.PresetCategory
import com.convxy.music.constants.PresetKeys
import com.convxy.music.constants.presetKeyTypes
import com.convxy.music.ui.player.customize.DIY_MAX_STICKERS
import com.convxy.music.ui.player.customize.DiyLayout
import com.convxy.music.ui.player.customize.DiyStickerKind
import com.convxy.music.ui.player.customize.DiyStore
import com.convxy.music.ui.player.customize.PlayerIconSet
import com.convxy.music.ui.player.customize.PlayerIconStore
import com.convxy.music.ui.theme.customFontDir
import com.convxy.music.utils.MediaImport
import com.convxy.music.utils.dataStore
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.UUID
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/**
 * Named, shareable snapshots of everything visual: colours, layout, fonts, the player's look,
 * lyrics styling, glass, wallpapers, custom control glyphs, and the DIY sticker arrangement.
 *
 * A preset on disk is a directory; a preset in transit is a zip of that directory. Assets travel
 * as real files rather than base64 inside the manifest, so a wallpaper costs its own size instead
 * of a third more, and never has to be held in memory as a String to be read.
 */
object PresetStore {

    const val FILE_EXTENSION = "convxpreset"
    private const val MANIFEST = "manifest.json"
    private const val THUMB = "thumb.webp"
    private const val ASSETS = "assets"
    private const val FORMAT_VERSION = 1

    /**
     * The archive's asset directory is flat, and a V1 and a V2 slot can produce the same
     * file name, so V2 glyphs are stored under a prefix. Anything that walks the asset
     * directory has to know about it -- see [sanitiseAssets].
     */
    private const val V2_ASSET_PREFIX = "v2_"


    // --- Import limits. A preset can arrive from anywhere, so every one of these is a hard stop.
    private const val MAX_ARCHIVE_BYTES = 20L * 1024 * 1024
    private const val MAX_ENTRIES = 64
    private const val MAX_ENTRY_BYTES = 25L * 1024 * 1024
    private const val MAX_TOTAL_UNCOMPRESSED = 60L * 1024 * 1024

    data class Meta(
        val id: String,
        val name: String,
        val createdAt: Long,
        val categories: Set<PresetCategory>,
    )

    sealed interface ImportResult {
        data class Ok(val meta: Meta) : ImportResult
        data class Failed(val reason: Reason) : ImportResult
        enum class Reason { TOO_LARGE, MALFORMED, UNSUPPORTED_VERSION, IO }
    }

    fun rootDir(context: Context): File = File(context.filesDir, "presets").apply { mkdirs() }

    private fun dirFor(context: Context, id: String): File = File(rootDir(context), id)

    fun thumbnailFile(context: Context, id: String): File? =
        File(dirFor(context, id), THUMB).takeIf { it.isFile }

    fun list(context: Context): List<Meta> =
        rootDir(context).listFiles()
            ?.filter { it.isDirectory }
            ?.mapNotNull { dir -> readMeta(File(dir, MANIFEST)) }
            ?.sortedByDescending { it.createdAt }
            .orEmpty()

    private fun readMeta(manifest: File): Meta? = runCatching {
        val obj = JSONObject(manifest.readText())
        Meta(
            id = obj.getString("id"),
            name = obj.optString("name").ifEmpty { "Preset" },
            createdAt = obj.optLong("createdAt"),
            categories = obj.optJSONArray("categories").toCategorySet(),
        )
    }.getOrNull()

    // ---------------------------------------------------------------- capture

    /**
     * Snapshots the current look into a new preset.
     *
     * @param categories which parts to record. Anything not listed is simply absent from the
     *   preset, so applying it later leaves those settings untouched.
     * @param thumbnail a render of the DIY player mockup, recompressed here rather than stored raw.
     */
    suspend fun capture(
        context: Context,
        name: String,
        categories: Set<PresetCategory>,
        thumbnail: Bitmap?,
    ): Meta {
        val id = UUID.randomUUID().toString()
        val dir = dirFor(context, id).apply { mkdirs() }
        val assetDir = File(dir, ASSETS).apply { mkdirs() }

        val prefs = context.dataStore.data.first()
        val entries = JSONArray()

        categories.mapNotNull { PresetKeys.byCategory[it] }.flatten().forEach { key ->
            val value = prefs[key] ?: return@forEach
            val type = presetKeyTypes[key.name] ?: return@forEach

            val stored: Any = if (key.name in PresetKeys.fileBackedKeys) {
                val source = File(value as? String ?: return@forEach)
                if (!source.isFile) return@forEach
                val assetName = "${key.name}_${source.name}"
                source.copyTo(File(assetDir, assetName), overwrite = true)
                "asset:$assetName"
            } else {
                value
            }
            entries.put(encode(key.name, type, stored))
        }

        val manifest = JSONObject()
            .put("version", FORMAT_VERSION)
            .put("id", id)
            .put("name", name)
            .put("createdAt", System.currentTimeMillis())
            .put("categories", JSONArray().apply { categories.forEach { put(it.name) } })
            .put("prefs", entries)

        if (PresetCategory.PLAYER_ICONS in categories) {
            val set = PlayerIconStore.load(context)
            set.overrides.values.forEach { override ->
                val source = PlayerIconStore.fileFor(context, override)
                if (source.isFile) source.copyTo(File(assetDir, override.fileName), overwrite = true)
            }
            manifest.put("playerIcons", JSONObject(set.toJson()))

            // The V2 (Apple-style) player keeps its glyphs under a separate key and a
            // separate directory. Without this a preset silently dropped every icon the
            // user had chosen there. Asset names are prefixed because the archive is one
            // flat directory and a V1 and V2 slot can produce the same file name.
            val v2Set = PlayerIconStore.load(context, V2PlayerIconsKey)
            v2Set.overrides.values.forEach { override ->
                val source = PlayerIconStore.fileFor(context, override, isV2 = true)
                if (source.isFile) {
                    source.copyTo(File(assetDir, V2_ASSET_PREFIX + override.fileName), overwrite = true)
                }
            }
            manifest.put("v2PlayerIcons", JSONObject(v2Set.toJson()))
        }
        if (PresetCategory.DIY in categories) {
            val layout = DiyStore.load(context)
            layout.stickers.forEach { sticker ->
                DiyStore.stickerFile(context, sticker)
                    ?.takeIf { it.isFile }
                    ?.copyTo(File(assetDir, sticker.source), overwrite = true)
            }
            manifest.put("diy", JSONObject(layout.toJson()))
        }

        File(dir, MANIFEST).writeText(manifest.toString())
        thumbnail?.let { writeThumbnail(File(dir, THUMB), it) }

        return Meta(id, name, System.currentTimeMillis(), categories)
    }

    private fun writeThumbnail(dest: File, bitmap: Bitmap) {
        runCatching {
            // A layer capture can come back as a HARDWARE bitmap, which has no pixel data to read
            // on the CPU — compressing one throws. Copying to ARGB_8888 first is cheap at
            // thumbnail size and makes the write work regardless of where the bitmap came from.
            val source = if (bitmap.config == Bitmap.Config.HARDWARE) {
                bitmap.copy(Bitmap.Config.ARGB_8888, false) ?: return
            } else {
                bitmap
            }
            dest.outputStream().use { out ->
                @Suppress("DEPRECATION")
                source.compress(Bitmap.CompressFormat.WEBP, 80, out)
            }
            if (source !== bitmap) source.recycle()
        }
    }

    // ------------------------------------------------------------------ apply

    /**
     * Writes the preset's values back into preferences.
     *
     * @param categories the subset the user ticked. Categories the preset does not contain are
     *   ignored rather than reset — applying a colours-only preset must not wipe someone's fonts.
     */
    suspend fun apply(context: Context, id: String, categories: Set<PresetCategory>) {
        val dir = dirFor(context, id)
        val manifest = runCatching { JSONObject(File(dir, MANIFEST).readText()) }.getOrNull()
            ?: return
        val assetDir = File(dir, ASSETS)

        val wanted = categories.mapNotNull { PresetKeys.byCategory[it] }.flatten()
            .mapTo(mutableSetOf()) { it.name }

        val prefsArray = manifest.optJSONArray("prefs") ?: JSONArray()
        context.dataStore.edit { store ->
            for (i in 0 until prefsArray.length()) {
                val entry = prefsArray.optJSONObject(i) ?: continue
                val name = entry.optString("n").takeIf { it in wanted } ?: continue
                val declared = presetKeyTypes[name] ?: continue
                val type = runCatching { PrefType.valueOf(entry.optString("t")) }.getOrNull()
                // A preset that disagrees with the app about a key's type is malformed or
                // hostile; writing it anyway would plant a ClassCastException in an unrelated
                // screen, so the entry is dropped instead.
                if (type != declared) continue

                if (name in PresetKeys.fileBackedKeys) {
                    val installed = installAsset(context, assetDir, entry.optString("v"), name)
                        ?: continue
                    store[stringPreferencesKey(name)] = installed
                } else {
                    writeTyped(store, name, declared, entry)
                }
            }
        }

        if (PresetCategory.PLAYER_ICONS in categories) {
            manifest.optJSONObject("playerIcons")?.let { icons ->
                val set = PlayerIconSet.fromJson(icons.toString())
                val dest = PlayerIconStore.dir(context)
                set.overrides.values.forEach { override ->
                    File(assetDir, override.fileName).takeIf { it.isFile }
                        ?.copyTo(File(dest, override.fileName), overwrite = true)
                }
                context.dataStore.edit { it[PlayerIconsKey] = set.toJson() }
                PlayerIconStore.pruneOrphans(context, set)
            }
            // Absent from presets written before V2 existed, so a missing block leaves
            // whatever the user already had rather than clearing it.
            manifest.optJSONObject("v2PlayerIcons")?.let { icons ->
                val v2Set = PlayerIconSet.fromJson(icons.toString())
                val dest = PlayerIconStore.v2Dir(context)
                v2Set.overrides.values.forEach { override ->
                    File(assetDir, V2_ASSET_PREFIX + override.fileName).takeIf { it.isFile }
                        ?.copyTo(File(dest, override.fileName), overwrite = true)
                }
                context.dataStore.edit { it[V2PlayerIconsKey] = v2Set.toJson() }
                PlayerIconStore.pruneOrphans(context, v2Set, isV2 = true)
            }
        }
        if (PresetCategory.DIY in categories) {
            manifest.optJSONObject("diy")?.let { diy ->
                val layout = DiyLayout.fromJson(diy.toString())
                val dest = DiyStore.stickerDir(context)
                layout.stickers.filter { it.kind == DiyStickerKind.IMAGE }.forEach { sticker ->
                    File(assetDir, sticker.source).takeIf { it.isFile }
                        ?.copyTo(File(dest, sticker.source), overwrite = true)
                }
                context.dataStore.edit { it[DiyLayoutKey] = layout.toJson() }
                DiyStore.pruneOrphans(context, layout)
            }
        }
    }

    /** Copies a packaged asset into the directory the live setting expects, returning its path. */
    private fun installAsset(
        context: Context,
        assetDir: File,
        value: String,
        keyName: String,
    ): String? {
        val assetName = value.removePrefix("asset:").takeIf { it != value } ?: return null
        if (!isSafeEntryName(assetName)) return null
        val source = File(assetDir, assetName).takeIf { it.isFile } ?: return null

        val destDir = when (keyName) {
            HomeBackgroundPathKey.name -> context.filesDir
            CustomFontPathKey.name -> customFontDir(context)
            else -> return null
        }
        val dest = File(destDir, assetName)
        return runCatching { source.copyTo(dest, overwrite = true).absolutePath }.getOrNull()
    }

    private fun writeTyped(
        store: androidx.datastore.preferences.core.MutablePreferences,
        name: String,
        type: PrefType,
        entry: JSONObject,
    ) {
        when (type) {
            PrefType.BOOL -> store[booleanPreferencesKey(name)] = entry.optBoolean("v")
            PrefType.INT -> store[intPreferencesKey(name)] = entry.optInt("v")
            PrefType.FLOAT -> store[floatPreferencesKey(name)] = entry.optDouble("v").toFloat()
            PrefType.LONG -> store[longPreferencesKey(name)] = entry.optLong("v")
            PrefType.STRING -> store[stringPreferencesKey(name)] = entry.optString("v")
            PrefType.STRING_SET -> {
                val arr = entry.optJSONArray("v") ?: return
                store[stringSetPreferencesKey(name)] =
                    (0 until arr.length()).mapNotNull { arr.optString(it).takeIf(String::isNotEmpty) }
                        .toSet()
            }
        }
    }

    private fun encode(name: String, type: PrefType, value: Any): JSONObject =
        JSONObject().put("n", name).put("t", type.name).apply {
            if (value is Set<*>) put("v", JSONArray().apply { value.forEach { put(it) } })
            else put("v", value)
        }

    // ----------------------------------------------------------------- delete

    fun delete(context: Context, id: String) {
        dirFor(context, id).deleteRecursively()
    }

    fun rename(context: Context, id: String, name: String) {
        val manifest = File(dirFor(context, id), MANIFEST)
        runCatching {
            val obj = JSONObject(manifest.readText()).put("name", name)
            manifest.writeText(obj.toString())
        }
    }

    // ----------------------------------------------------------------- export

    /** Zips a preset into the cache directory, ready to hand to a share sheet. */
    fun export(context: Context, meta: PresetStore.Meta): File? = runCatching {
        val dir = dirFor(context, meta.id)
        val safeName = meta.name.replace(Regex("[^A-Za-z0-9 _-]"), "").trim().ifEmpty { "preset" }
        val out = File(context.cacheDir, "shared_presets").apply { mkdirs() }
            .resolve("$safeName.$FILE_EXTENSION")

        ZipOutputStream(out.outputStream().buffered()).use { zip ->
            dir.walkTopDown().filter { it.isFile }.forEach { file ->
                val relative = file.relativeTo(dir).invariantSeparatorsPath
                zip.putNextEntry(ZipEntry(relative))
                file.inputStream().use { it.copyTo(zip) }
                zip.closeEntry()
            }
        }
        out
    }.getOrNull()

    // ----------------------------------------------------------------- import

    /**
     * Unpacks a shared preset.
     *
     * Everything here treats the archive as hostile: paths are checked for traversal, entry
     * counts and sizes are capped in both compressed and uncompressed terms, the manifest is
     * re-parsed through the same allowlist as any other preset, and every bundled image is
     * re-decoded and re-encoded by [MediaImport] rather than trusted as-is.
     */
    suspend fun import(context: Context, uri: Uri): ImportResult {
        val archiveSize = runCatching {
            context.contentResolver.openAssetFileDescriptor(uri, "r")?.use { it.length }
        }.getOrNull()
        if (archiveSize != null && archiveSize > MAX_ARCHIVE_BYTES) {
            return ImportResult.Failed(ImportResult.Reason.TOO_LARGE)
        }

        val id = UUID.randomUUID().toString()
        val dir = dirFor(context, id).apply { mkdirs() }

        val unpacked = runCatching { unpack(context, uri, dir) }.getOrElse { false }
        if (!unpacked) {
            dir.deleteRecursively()
            return ImportResult.Failed(ImportResult.Reason.TOO_LARGE)
        }

        val manifestFile = File(dir, MANIFEST)
        val manifest = runCatching { JSONObject(manifestFile.readText()) }.getOrNull()
        if (manifest == null) {
            dir.deleteRecursively()
            return ImportResult.Failed(ImportResult.Reason.MALFORMED)
        }
        if (manifest.optInt("version", 0) > FORMAT_VERSION) {
            dir.deleteRecursively()
            return ImportResult.Failed(ImportResult.Reason.UNSUPPORTED_VERSION)
        }

        // Re-key onto this device so two copies of the same shared preset can coexist.
        manifest.put("id", id)
        if (!manifest.has("createdAt")) manifest.put("createdAt", System.currentTimeMillis())
        val meta = Meta(
            id = id,
            name = manifest.optString("name").ifEmpty { "Preset" }.take(60),
            createdAt = manifest.optLong("createdAt", System.currentTimeMillis()),
            categories = manifest.optJSONArray("categories").toCategorySet(),
        )
        manifest.put("name", meta.name)
        manifestFile.writeText(manifest.toString())

        sanitiseAssets(context, File(dir, ASSETS), manifest)
        sanitiseThumbnail(context, dir)
        return ImportResult.Ok(meta)
    }

    /** @return false if the archive breached any limit; the caller discards the directory. */
    private fun unpack(context: Context, uri: Uri, dir: File): Boolean {
        val canonicalRoot = dir.canonicalPath
        var entries = 0
        var total = 0L

        context.contentResolver.openInputStream(uri).use { raw ->
            raw ?: return false
            ZipInputStream(raw.buffered()).use { zip ->
                while (true) {
                    val entry: ZipEntry = zip.nextEntry ?: break
                    if (++entries > MAX_ENTRIES) return false
                    if (entry.isDirectory) { zip.closeEntry(); continue }
                    if (!isSafeEntryPath(entry.name)) return false

                    val dest = File(dir, entry.name)
                    // Belt and braces: even with the name check, resolve and compare. A symlinked
                    // or unicode-normalised path that slips the string check dies here.
                    if (!dest.canonicalPath.startsWith(canonicalRoot + File.separator)) return false
                    dest.parentFile?.mkdirs()

                    var written = 0L
                    dest.outputStream().buffered().use { out ->
                        val buffer = ByteArray(16 * 1024)
                        while (true) {
                            val read = zip.read(buffer)
                            if (read <= 0) break
                            written += read
                            total += read
                            if (written > MAX_ENTRY_BYTES || total > MAX_TOTAL_UNCOMPRESSED) {
                                return false
                            }
                            out.write(buffer, 0, read)
                        }
                    }
                    zip.closeEntry()
                }
            }
        }
        return File(dir, MANIFEST).isFile
    }

    /**
     * Re-runs every bundled image through the normal import pipeline and deletes anything that
     * is not referenced by the manifest. After this the asset directory contains only files this
     * app itself encoded.
     */
    private fun sanitiseAssets(context: Context, assetDir: File, manifest: JSONObject) {
        if (!assetDir.isDirectory) return

        val referenced = buildSet {
            manifest.optJSONArray("prefs")?.let { arr ->
                for (i in 0 until arr.length()) {
                    val v = arr.optJSONObject(i)?.optString("v").orEmpty()
                    if (v.startsWith("asset:")) add(v.removePrefix("asset:"))
                }
            }
            manifest.optJSONObject("playerIcons")?.let { icons ->
                icons.keys().forEach { slot ->
                    icons.optJSONObject(slot)?.optString("file")
                        ?.takeIf { it.isNotEmpty() }?.let(::add)
                }
            }
            manifest.optJSONObject("v2PlayerIcons")?.let { icons ->
                icons.keys().forEach { slot ->
                    icons.optJSONObject(slot)?.optString("file")
                        ?.takeIf { it.isNotEmpty() }
                        ?.let { add(V2_ASSET_PREFIX + it) }
                }
            }
            manifest.optJSONObject("diy")?.optJSONArray("stickers")?.let { arr ->
                for (i in 0 until arr.length().coerceAtMost(DIY_MAX_STICKERS)) {
                    val sticker = arr.optJSONObject(i) ?: continue
                    if (sticker.optString("kind") == DiyStickerKind.IMAGE.name) {
                        sticker.optString("source").takeIf { it.isNotEmpty() }?.let(::add)
                    }
                }
            }
        }

        assetDir.listFiles()?.forEach { file ->
            if (file.name !in referenced) { file.delete(); return@forEach }
            when (file.extension.lowercase()) {
                "ttf", "otf" -> Unit // Fonts are handed to the platform loader, not decoded here.
                "svg" -> if (!MediaImport.isSafeSvg(runCatching { file.readText() }
                        .getOrDefault(""))) file.delete()
                else -> revalidateImage(context, file)
            }
        }
    }

    private fun revalidateImage(context: Context, file: File) {
        val result = MediaImport.import(
            context = context,
            uri = Uri.fromFile(file),
            kind = MediaImport.Kind.STICKER,
            destDir = file.parentFile ?: return,
            baseName = "revalidate_${file.nameWithoutExtension}",
            allowVector = false,
        )
        when (result) {
            is MediaImport.Result.Ok -> {
                file.delete()
                result.file.renameTo(file)
            }
            is MediaImport.Result.Failed -> file.delete()
        }
    }

    private fun sanitiseThumbnail(context: Context, dir: File) {
        val thumb = File(dir, THUMB).takeIf { it.isFile } ?: return
        val result = MediaImport.import(
            context = context,
            uri = Uri.fromFile(thumb),
            kind = MediaImport.Kind.THUMBNAIL,
            destDir = dir,
            baseName = "thumb_checked",
            allowVector = false,
        )
        thumb.delete()
        if (result is MediaImport.Result.Ok) result.file.renameTo(thumb)
    }

    private fun isSafeEntryPath(name: String): Boolean {
        if (name.isEmpty() || name.length > 256) return false
        if (name.startsWith("/") || name.contains("..") || name.contains('\\')) return false
        if (name.contains(':')) return false
        val parts = name.split('/')
        if (parts.size > 2) return false
        return when (parts.size) {
            1 -> parts[0] == MANIFEST || parts[0] == THUMB
            else -> parts[0] == ASSETS && isSafeEntryName(parts[1])
        }
    }

    private fun isSafeEntryName(name: String): Boolean =
        name.isNotEmpty() && name.length <= 128 &&
            !name.contains('/') && !name.contains('\\') && !name.contains("..")

    private fun JSONArray?.toCategorySet(): Set<PresetCategory> {
        this ?: return emptySet()
        return (0 until length()).mapNotNullTo(mutableSetOf()) { i ->
            runCatching { PresetCategory.valueOf(optString(i)) }.getOrNull()
        }
    }
}
