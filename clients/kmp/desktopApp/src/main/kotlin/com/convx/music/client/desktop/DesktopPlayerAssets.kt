package com.convx.music.client.desktop

import com.convx.music.playback.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import java.io.File
import java.security.MessageDigest
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import javax.imageio.ImageIO

@Serializable
internal data class DesktopVisualPreset(
    val version: Int = 1,
    val name: String = "Convx preset",
    val appearance: AppearanceSettings = AppearanceSettings(),
    val player: PlayerCustomization = PlayerCustomization(),
    val darkTheme: Boolean = false,
    val lyricsFontSize: Float = 30f,
)

internal class DesktopPlayerAssets(private val directory: File) {
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    fun file(name: String): File? = name.takeIf(::isSafePlayerAsset)?.let { directory.resolve(it) }?.takeIf(File::isFile)

    fun install(source: File, customization: PlayerCustomization): String {
        require(source.isFile && source.length() in 1..DIY_MAX_ASSET_BYTES) { "Choose an image smaller than 8 MB." }
        val bytes = source.readBytes()
        validateImage(bytes)
        val name = assetName(bytes)
        val referenced = customization.assets() + name
        val total = referenced.sumOf { if (it == name) bytes.size.toLong() else file(it)?.length() ?: 0 }
        require(total <= DIY_MAX_ASSET_BYTES) { "The player's images can use up to 8 MB. Remove an image first." }
        writeAsset(name, bytes)
        return name
    }

    fun exportPreset(destination: File, preset: DesktopVisualPreset) {
        val player = preset.player.normalized()
        val manifest = buildJsonObject {
            put("version", 1); put("id", java.util.UUID.randomUUID().toString()); put("name", preset.name.take(120))
            put("createdAt", System.currentTimeMillis())
            put("categories", JsonArray(listOf("COLORS", "PLAYER", "PLAYER_ICONS", "DIY").map(::JsonPrimitive)))
            put("desktop", json.encodeToJsonElement(preset.copy(player = player)))
            put("diy", json.encodeToJsonElement(player.diy))
            put("playerIcons", json.encodeToJsonElement(player.icons))
            put("prefs", buildJsonArray {
                add(pref("playerLayoutOrder", "STRING", JsonPrimitive(player.order.joinToString(",") { it.name })))
                add(pref("playerLayoutHiddenSlots", "STRING", JsonPrimitive(player.hidden.joinToString(",") { it.name })))
            })
        }
        val staging = File.createTempFile("convx-preset-", ".tmp", destination.absoluteFile.parentFile)
        try {
            ZipOutputStream(staging.outputStream()).use { zip ->
                zip.putNextEntry(ZipEntry("manifest.json")); zip.write(manifest.toString().toByteArray()); zip.closeEntry()
                player.assets().forEach { name ->
                    val source = requireNotNull(file(name)) { "A player image is missing. Remove it before exporting." }
                    zip.putNextEntry(ZipEntry("assets/$name")); source.inputStream().use { it.copyTo(zip) }; zip.closeEntry()
                }
            }
            java.nio.file.Files.move(staging.toPath(), destination.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING)
        } finally { staging.delete() }
    }

    fun importPreset(source: File): DesktopVisualPreset {
        require(source.length() in 1..20L * 1024 * 1024) { "Choose a Convx preset smaller than 20 MB." }
        val entries = linkedMapOf<String, ByteArray>()
        var total = 0L
        ZipInputStream(source.inputStream().buffered()).use { zip ->
            while (true) {
                val entry = zip.nextEntry ?: break
                val name = entry.name
                require(entries.size < 64 && !entries.containsKey(name)) { "The preset contains too many or duplicate files." }
                require(name == "manifest.json" || name == "thumb.webp" ||
                    (name.startsWith("assets/") && isSafePlayerAsset(name.removePrefix("assets/")))) { "The preset contains an invalid file path." }
                require(!entry.isDirectory) { "The preset contains an invalid folder." }
                val limit = if (name == "manifest.json") 512 * 1024 else DIY_MAX_ASSET_BYTES.toInt()
                val bytes = zip.readNBytes(limit + 1)
                require(bytes.size <= limit) { "A preset file is too large." }
                total += bytes.size
                require(total <= 40L * 1024 * 1024) { "The unpacked preset is too large." }
                entries[name] = bytes
            }
        }
        val manifest = json.parseToJsonElement(requireNotNull(entries["manifest.json"]) { "The preset has no manifest." }.decodeToString()).jsonObject
        require(manifest["version"]?.jsonPrimitive?.int == 1) { "This preset version isn't supported." }
        val desktop = manifest["desktop"]?.let { json.decodeFromJsonElement<DesktopVisualPreset>(it) }
        val preset = desktop ?: importAndroidPreset(manifest)
        val player = preset.player.normalized()
        val assets = player.assets().associateWith { name ->
            requireNotNull(entries["assets/$name"]) { "The preset is missing an image." }.also(::validateImage)
        }
        require(assets.values.sumOf { it.size.toLong() } <= DIY_MAX_ASSET_BYTES) { "The player's images exceed 8 MB." }
        val installed = assets.mapValues { (_, bytes) -> assetName(bytes).also { writeAsset(it, bytes) } }
        return preset.copy(appearance = preset.appearance.copy(customFontFile = null).normalized(),
            lyricsFontSize = preset.lyricsFontSize.takeIf(Float::isFinite)?.coerceIn(18f, 48f) ?: 30f,
            player = player.copy(diy = player.diy.copy(stickers = player.diy.stickers.map {
                if (it.kind == DiyStickerKind.IMAGE) it.copy(source = installed.getValue(it.source)) else it
            }), icons = player.icons.mapValues { (_, icon) -> icon.copy(fileName = installed.getValue(icon.fileName)) }))
    }

    private fun importAndroidPreset(manifest: JsonObject): DesktopVisualPreset {
        val diy = manifest["diy"]?.let { json.decodeFromJsonElement<DiyLayout>(it) } ?: DiyLayout()
        val icons = manifest["playerIcons"]?.let { json.decodeFromJsonElement<Map<String, PlayerIconOverride>>(it) }.orEmpty().toMutableMap()
        manifest["v2PlayerIcons"]?.jsonObject?.forEach { (key, value) ->
            val mapped = key.removePrefix("V2_")
            if (mapped in PLAYER_ICON_SLOTS && mapped !in icons) {
                val icon = json.decodeFromJsonElement<PlayerIconOverride>(value)
                icons[mapped] = icon.copy(fileName = "v2_${icon.fileName}")
            }
        }
        val prefs = manifest["prefs"]?.jsonArray.orEmpty().associate { entry ->
            entry.jsonObject.getValue("n").jsonPrimitive.content to entry.jsonObject.getValue("v")
        }
        val order = prefs["playerLayoutOrder"]?.jsonPrimitive?.content?.split(',')?.mapNotNull { name ->
            PlayerSection.entries.find { it.name == name.trim() }
        } ?: PlayerSection.entries.toList()
        val hidden = prefs["playerLayoutHiddenSlots"]?.jsonPrimitive?.content?.split(',')?.mapNotNull { name ->
            PlayerSection.entries.find { it.name == name.trim() }
        }?.toSet().orEmpty()
        return DesktopVisualPreset(name = manifest["name"]?.jsonPrimitive?.content ?: "Imported preset",
            player = PlayerCustomization(order = order, hidden = hidden, diy = diy, icons = icons))
    }

    private fun writeAsset(name: String, bytes: ByteArray) {
        directory.mkdirs()
        val target = directory.resolve(name)
        if (!target.isFile) target.writeBytes(bytes)
    }

    private fun assetName(bytes: ByteArray) = MessageDigest.getInstance("SHA-256").digest(bytes)
        .joinToString("") { "%02x".format(it) } + ".png"

    private fun validateImage(bytes: ByteArray) {
        ImageIO.createImageInputStream(bytes.inputStream()).use { input ->
            val readers = ImageIO.getImageReaders(input)
            require(readers.hasNext()) { "Use a PNG, JPEG, GIF, or BMP image." }
            val reader = readers.next()
            try {
                reader.input = input
                val width = reader.getWidth(0); val height = reader.getHeight(0)
                require(width in 1..8192 && height in 1..8192 && width.toLong() * height <= 32_000_000) { "Choose an image up to 8192 pixels and 32 megapixels." }
            } finally { reader.dispose() }
        }
    }

    private fun pref(name: String, type: String, value: JsonElement) = buildJsonObject { put("n", name); put("t", type); put("v", value) }
}

private fun PlayerCustomization.assets() = (diy.stickers.filter { it.kind == DiyStickerKind.IMAGE }.map { it.source } + icons.values.map { it.fileName }).toSet()
