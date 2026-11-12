package com.music.innertube.mappers

import com.music.innertube.models.Album
import com.music.innertube.models.AlbumItem
import com.music.innertube.models.Artist
import com.music.innertube.models.SongItem
import com.music.innertube.pages.AlbumPage
import com.music.innertube.utils.parseTime
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull

/**
 * Reads an album straight off the raw browse JSON, finding renderers wherever they sit.
 * Used when the typed [AlbumMapper.parseAlbum] cannot decode a page: a layout change then
 * costs a field, not the whole album.
 */
internal object LenientAlbumParser {
    private val headerNames = listOf("musicResponsiveHeaderRenderer", "musicDetailHeaderRenderer", "musicImmersiveHeaderRenderer")
    private val duration = Regex("\\d+:\\d{2}(?::\\d{2})?")

    fun parse(root: JsonElement, browseId: String): AlbumPage? {
        val header = headerNames.firstNotNullOfOrNull { collect(root, it).firstOrNull() }
        val title = header?.obj("title")?.runs()?.firstOrNull()?.text()
            ?: root.obj("microformat")?.obj("microformatDataRenderer")?.str("title")?.removeSuffix(" - YouTube Music")?.trim()
        val artists = (header?.obj("straplineTextOne")?.runs() ?: header?.obj("subtitle")?.runs()).orEmpty()
            .filter { it.browseId() != null }
            .map { Artist(it.text(), it.browseId()) }
        val thumbnail = header?.let(::thumbnailOf).orEmpty()
        val album = AlbumItem(
            browseId = browseId,
            playlistId = collect(root, "watchPlaylistEndpoint").firstNotNullOfOrNull { it.str("playlistId") }
                ?: collect(root, "musicPlaylistShelfRenderer").firstNotNullOfOrNull { it.str("playlistId") }
                ?: "",
            title = title.orEmpty(),
            artists = artists,
            year = header?.obj("subtitle")?.runs()?.lastOrNull()?.text()?.toIntOrNull(),
            thumbnail = thumbnail,
        )
        // Recommendation shelves hold list rows too; only the album's own shelf counts, when there is one.
        val shelves = collect(root, "musicPlaylistShelfRenderer") + collect(root, "musicShelfRenderer")
        val rows = shelves.firstOrNull { shelf -> collect(shelf, "musicResponsiveListItemRenderer").any { it.obj("playlistItemData") != null } }
            ?.let { collect(it, "musicResponsiveListItemRenderer") }
            ?: collect(root, "musicResponsiveListItemRenderer")
        val songs = rows.mapNotNull { song(it, album) }.distinctBy { it.id }
        if (album.title.isBlank() && songs.isEmpty()) return null
        return AlbumPage(album = album, songs = songs, otherVersions = emptyList())
    }

    private fun song(row: JsonObject, album: AlbumItem): SongItem? {
        val id = row.obj("playlistItemData")?.str("videoId")
            ?: collect(row, "watchEndpoint").firstNotNullOfOrNull { it.str("videoId") }
            ?: return null
        val columns = row.arr("flexColumns").orEmpty().mapNotNull { it.obj("musicResponsiveListItemFlexColumnRenderer")?.obj("text")?.runs() }
        val title = columns.firstOrNull()?.joinToString("") { it.text() }?.takeIf { it.isNotBlank() } ?: return null
        val rowArtists = columns.drop(1).flatten().filter { it.browseId()?.startsWith("UC") == true }.map { Artist(it.text(), it.browseId()) }
        val time = row.arr("fixedColumns").orEmpty()
            .flatMap { it.obj("musicResponsiveListItemFixedColumnRenderer")?.obj("text")?.runs().orEmpty() }
            .plus(columns.drop(1).flatten())
            .firstOrNull { duration.matches(it.text()) }?.text()?.parseTime()
        return SongItem(
            id = id,
            title = title,
            artists = rowArtists.ifEmpty { album.artists.orEmpty() },
            album = album.title.takeIf { it.isNotBlank() }?.let { Album(it, album.browseId) },
            duration = time,
            thumbnail = thumbnailOf(row).ifBlank { album.thumbnail },
        )
    }

    private fun thumbnailOf(node: JsonObject): String =
        node.findArray("thumbnails")?.lastOrNull()?.let { (it as? JsonObject)?.str("url") }.orEmpty()

    private fun JsonObject.findArray(key: String): JsonArray? {
        var found: JsonArray? = null
        fun walk(node: JsonElement) {
            if (found != null) return
            when (node) {
                is JsonObject -> { (node[key] as? JsonArray)?.let { found = it } ; node.values.forEach(::walk) }
                is JsonArray -> node.forEach(::walk)
                else -> Unit
            }
        }
        walk(this)
        return found
    }

    /** Depth-first, document order. */
    private fun collect(root: JsonElement, name: String): List<JsonObject> {
        val out = ArrayList<JsonObject>()
        fun walk(node: JsonElement) {
            when (node) {
                is JsonObject -> { (node[name] as? JsonObject)?.let(out::add); node.values.forEach(::walk) }
                is JsonArray -> node.forEach(::walk)
                else -> Unit
            }
        }
        walk(root)
        return out
    }

    private fun JsonElement.obj(key: String): JsonObject? = (this as? JsonObject)?.get(key) as? JsonObject
    private fun JsonElement.arr(key: String): List<JsonObject>? = ((this as? JsonObject)?.get(key) as? JsonArray)?.mapNotNull { it as? JsonObject }
    private fun JsonObject.str(key: String): String? = (get(key) as? JsonPrimitive)?.contentOrNull
    private fun JsonObject.runs(): List<JsonObject>? = arr("runs")
    private fun JsonObject.text(): String = str("text").orEmpty()
    private fun JsonObject.browseId(): String? = obj("navigationEndpoint")?.obj("browseEndpoint")?.str("browseId")
}
