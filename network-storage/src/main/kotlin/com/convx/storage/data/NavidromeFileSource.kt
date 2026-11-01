package com.convx.storage.data

import com.convx.storage.domain.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.concurrent.TimeUnit
import kotlin.coroutines.coroutineContext

/**
 * Navidrome (and other Subsonic API servers) as a music source.
 *
 * Folders are the server's own artist, album and song listing. Paths are `artist/<id>`,
 * `album/<id>`, `song/<id>.<ext>` (the extension helps the player pick a decoder) and
 * `cover/<id>`.
 */
class NavidromeFileSource(val client: OkHttpClient = OkHttpClient.Builder()
    .followRedirects(false).followSslRedirects(false)
    .connectTimeout(15, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build()) : NetworkFileSource {

    override suspend fun list(server: NetworkServer, folder: String): List<NetworkEntry> = withContext(Dispatchers.IO) {
        val source = server.navidrome()
        val parts = RemotePath.normalize(folder).split('/').filter(String::isNotEmpty)
        when {
            parts.isEmpty() -> {
                val indexes = call(source, "getArtists").optJSONObject("artists")?.items("index").orEmpty()
                indexes.flatMap { it.items("artist") }.mapNotNull { artist ->
                    val id = artist.optString("id").takeIf(String::isNotBlank) ?: return@mapNotNull null
                    NetworkEntry("artist/$id", artist.optString("name").ifBlank { id }, true)
                }.sortedBy { it.name.lowercase() }
            }
            parts[0] == "artist" && parts.size == 2 -> {
                call(source, "getArtist", "id" to parts[1]).optJSONObject("artist")?.items("album").orEmpty()
                    .mapNotNull { album ->
                        val id = album.optString("id").takeIf(String::isNotBlank) ?: return@mapNotNull null
                        NetworkEntry("album/$id", album.optString("name").ifBlank { album.optString("title").ifBlank { id } }, true)
                    }
            }
            parts[0] == "album" && parts.size == 2 -> {
                call(source, "getAlbum", "id" to parts[1]).optJSONObject("album")?.items("song").orEmpty()
                    .mapNotNull { song ->
                        val path = songPath(song) ?: return@mapNotNull null
                        NetworkEntry(path, song.optString("title").ifBlank { path } + "." + suffix(song), false, song.optLong("size", -1))
                    }
            }
            else -> emptyList()
        }
    }

    override fun open(server: NetworkServer, path: String): RemoteFile {
        val source = server.navidrome()
        val parts = RemotePath.normalize(path).split('/')
        return when (parts.firstOrNull()) {
            "song" -> HttpRangeFile(client, streamUrl(source, path))
            "cover" -> {
                val url = signed(source, "getCoverArt", "id" to idOf(parts.getOrNull(1).orEmpty()), "size" to "800")
                client.newCall(Request.Builder().url(url).build()).execute().use { response ->
                    WebDavFileSource.checkStatus(response.code)
                    val body = response.body ?: throw StorageException(StorageError.INVALID_RESPONSE)
                    if (body.contentLength() > MaxCoverBytes) throw StorageException(StorageError.LIMIT_REACHED)
                    BytesRemoteFile(body.bytes())
                }
            }
            else -> throw StorageException(StorageError.NOT_FOUND)
        }
    }

    /** The address the player streams a song from, in its original quality. */
    fun streamUrl(server: NetworkServer.Navidrome, path: String): HttpUrl {
        val parts = RemotePath.normalize(path).split('/')
        if (parts.firstOrNull() != "song" || parts.size != 2) throw StorageException(StorageError.NOT_FOUND)
        return signed(server, "stream", "id" to idOf(parts[1]), "format" to "raw")
    }

    /** Every song on the server with its tags and cover art. */
    suspend fun scan(server: NetworkServer, maxTracks: Int): NetworkLibrary = withContext(Dispatchers.IO) {
        val source = server.navidrome()
        val tracks = ArrayList<NetworkTrack>()
        var truncated = false

        // Searching for nothing lists the whole library on Navidrome, one page at a time.
        for (query in listOf("\"\"", "")) {
            var offset = 0
            while (true) {
                coroutineContext.ensureActive()
                val page = call(
                    source, "search3", "query" to query, "artistCount" to "0", "albumCount" to "0",
                    "songCount" to PageSize.toString(), "songOffset" to offset.toString(),
                ).optJSONObject("searchResult3")?.items("song").orEmpty()
                if (page.isEmpty()) break
                for (song in page) {
                    if (tracks.size >= maxTracks) { truncated = true; break }
                    track(source.id, song)?.let { tracks.add(it) }
                }
                if (truncated || page.size < PageSize) break
                offset += page.size
            }
            if (tracks.isNotEmpty()) break
        }

        // Some servers ignore the empty search, so walk the albums instead.
        if (tracks.isEmpty()) {
            var offset = 0
            walk@ while (true) {
                coroutineContext.ensureActive()
                val albums = call(
                    source, "getAlbumList2", "type" to "alphabeticalByName",
                    "size" to PageSize.toString(), "offset" to offset.toString(),
                ).optJSONObject("albumList2")?.items("album").orEmpty()
                if (albums.isEmpty()) break
                for (album in albums) {
                    coroutineContext.ensureActive()
                    val id = album.optString("id").takeIf(String::isNotBlank) ?: continue
                    for (song in call(source, "getAlbum", "id" to id).optJSONObject("album")?.items("song").orEmpty()) {
                        if (tracks.size >= maxTracks) { truncated = true; break@walk }
                        track(source.id, song)?.let { tracks.add(it) }
                    }
                }
                if (albums.size < PageSize) break
                offset += albums.size
            }
        }
        NetworkLibrary(tracks, truncated)
    }

    override fun close() { }

    private fun track(serverId: String, song: JSONObject): NetworkTrack? {
        val path = songPath(song) ?: return null
        return NetworkTrack(
            id = NetworkMediaUri.create(serverId, path),
            title = song.optString("title").ifBlank { path },
            artist = song.optString("artist").takeIf(String::isNotBlank),
            album = song.optString("album").takeIf(String::isNotBlank),
            artwork = song.optString("coverArt").takeIf(String::isNotBlank)?.let { NetworkMediaUri.create(serverId, "cover/$it") },
            size = song.optLong("size", -1),
        )
    }

    private fun songPath(song: JSONObject): String? {
        val id = song.optString("id").takeIf(String::isNotBlank) ?: return null
        return "song/$id.${suffix(song)}"
    }

    private fun suffix(song: JSONObject): String =
        song.optString("suffix").lowercase().filter(Char::isLetterOrDigit).take(5).ifBlank { "mp3" }

    /** Removes the `.ext` that songPath adds, leaving the server's id. */
    private fun idOf(name: String): String {
        val dot = name.lastIndexOf('.')
        return if (dot > 0 && name.length - dot - 1 in 1..5 && name.substring(dot + 1).all(Char::isLetterOrDigit)) name.substring(0, dot) else name
    }

    private fun NetworkServer.navidrome(): NetworkServer.Navidrome =
        this as? NetworkServer.Navidrome ?: throw StorageException(StorageError.INVALID_CONFIGURATION)

    private fun signed(server: NetworkServer.Navidrome, method: String, vararg params: Pair<String, String>, json: Boolean = false): HttpUrl {
        val salt = ByteArray(8).also { SecureRandom().nextBytes(it) }.joinToString("") { "%02x".format(it) }
        val token = MessageDigest.getInstance("MD5")
            .digest((server.credentials.password + salt).toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }
        return server.address.toHttpUrl().newBuilder()
            .addPathSegments("rest/$method.view")
            .addQueryParameter("u", server.credentials.username)
            .addQueryParameter("t", token)
            .addQueryParameter("s", salt)
            .addQueryParameter("v", "1.16.1")
            .addQueryParameter("c", "Convx")
            .apply {
                if (json) addQueryParameter("f", "json")
                params.forEach { (key, value) -> addQueryParameter(key, value) }
            }
            .build()
    }

    private fun call(server: NetworkServer.Navidrome, method: String, vararg params: Pair<String, String>): JSONObject {
        val request = Request.Builder().url(signed(server, method, *params, json = true)).build()
        val text = client.newCall(request).execute().use { response ->
            WebDavFileSource.checkStatus(response.code)
            val body = response.body ?: throw StorageException(StorageError.INVALID_RESPONSE)
            if (body.contentLength() > MaxJsonBytes) throw StorageException(StorageError.LIMIT_REACHED)
            body.string()
        }
        val root = try { JSONObject(text).getJSONObject("subsonic-response") }
        catch (error: JSONException) { throw StorageException(StorageError.INVALID_RESPONSE, error) }
        if (root.optString("status") != "ok") {
            throw StorageException(when (root.optJSONObject("error")?.optInt("code")) {
                40, 41, 44 -> StorageError.AUTHENTICATION
                70 -> StorageError.NOT_FOUND
                else -> StorageError.INVALID_RESPONSE
            })
        }
        return root
    }

    /** Lists under [name], whether the server sent an array or a single object. */
    private fun JSONObject.items(name: String): List<JSONObject> {
        optJSONArray(name)?.let { array -> return (0 until array.length()).mapNotNull { array.optJSONObject(it) } }
        return optJSONObject(name)?.let { listOf(it) }.orEmpty()
    }

    private companion object {
        const val PageSize = 500
        const val MaxJsonBytes = 32 * 1024 * 1024
        const val MaxCoverBytes = 16 * 1024 * 1024
    }
}

private class BytesRemoteFile(private val bytes: ByteArray) : RemoteFile {
    override val length: Long = bytes.size.toLong()

    override fun read(position: Long, buffer: ByteArray, offset: Int, length: Int): Int {
        if (length == 0) return 0
        if (position >= bytes.size) return -1
        val count = minOf(length.toLong(), bytes.size - position).toInt()
        System.arraycopy(bytes, position.toInt(), buffer, offset, count)
        return count
    }

    override fun close() { }
}

private class HttpRangeFile(private val client: OkHttpClient, private val url: HttpUrl) : RemoteFile {
    private var response: Response? = null
    private var input: java.io.InputStream? = null
    private var cursor = 0L
    override var length = -1L
        private set

    override fun read(position: Long, buffer: ByteArray, offset: Int, length: Int): Int {
        if (length == 0) return 0
        require(position >= 0)
        if (input == null || position != cursor) {
            close()
            val result = client.newCall(Request.Builder().url(url).header("Range", "bytes=$position-").build()).execute()
            try {
                WebDavFileSource.checkStatus(result.code)
                val body = result.body ?: throw StorageException(StorageError.INVALID_RESPONSE)
                val stream = body.byteStream()
                if (result.code == 200 && position > 0) {
                    var remaining = position
                    val scratch = ByteArray(8192)
                    while (remaining > 0) {
                        val count = stream.read(scratch, 0, minOf(remaining, scratch.size.toLong()).toInt())
                        if (count < 0) throw StorageException(StorageError.NOT_FOUND)
                        remaining -= count
                    }
                }
                this.length = result.header("Content-Range")?.substringAfterLast('/')?.toLongOrNull()
                    ?: body.contentLength().takeIf { it >= 0 }?.let { if (result.code == 206) it + position else it } ?: -1
                response = result
                input = stream
                cursor = position
            } catch (error: Exception) { result.close(); throw error }
        }
        val count = requireNotNull(input).read(buffer, offset, length)
        if (count > 0) cursor += count
        return count
    }

    override fun close() { response?.close(); response = null; input = null }
}
