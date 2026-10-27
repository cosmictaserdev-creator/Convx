package com.convx.storage.data

import com.convx.storage.domain.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.w3c.dom.Element
import org.xml.sax.InputSource
import java.io.IOException
import java.io.StringReader
import java.util.concurrent.TimeUnit
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.coroutines.resumeWithException

class WebDavFileSource(val client: OkHttpClient = OkHttpClient.Builder()
    .followRedirects(false).followSslRedirects(false)
    .connectTimeout(15, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build()) : NetworkFileSource {

    // GET playback may redirect to a signed media URL. OkHttp removes Authorization
    // when the origin changes; PROPFIND retains its explicit non-redirecting policy.
    val playbackClient: OkHttpClient = client.newBuilder()
        .followRedirects(true).followSslRedirects(true).build()

    override suspend fun list(server: NetworkServer, folder: String): List<NetworkEntry> = withContext(Dispatchers.IO) {
        val source = server as? NetworkServer.WebDav ?: throw StorageException(StorageError.INVALID_CONFIGURATION)
        val resolved = resolve(source, folder)
        val url = if (resolved.encodedPath.endsWith('/')) resolved else resolved.newBuilder().addPathSegment("").build()
        val request = Request.Builder().url(url).header("Depth", "1")
            .method("PROPFIND", PropFindBody.toRequestBody("application/xml; charset=utf-8".toMediaType()))
            .apply { authorization(source)?.let { header("Authorization", it) } }.build()
        client.newCall(request).await().use { response ->
            checkStatus(response.code)
            val body = response.body ?: throw StorageException(StorageError.INVALID_RESPONSE)
            if (body.contentLength() > MaxXmlBytes) throw StorageException(StorageError.LIMIT_REACHED)
            val input = body.source()
            input.request(MaxXmlBytes + 1L)
            if (input.buffer.size > MaxXmlBytes) throw StorageException(StorageError.LIMIT_REACHED)
            val text = input.buffer.readUtf8()
            parseListing(source, url, text)
        }
    }

    override fun open(server: NetworkServer, path: String): RemoteFile {
        val source = server as? NetworkServer.WebDav ?: throw StorageException(StorageError.INVALID_CONFIGURATION)
        return HttpRemoteFile(playbackClient, resolve(source, path), authorization(source))
    }

    override fun close() { }

    companion object {
        private const val MaxXmlBytes = 2 * 1024 * 1024
        private val PropFindBody = """<?xml version="1.0" encoding="UTF-8"?><d:propfind xmlns:d="DAV:"><d:prop><d:displayname/><d:resourcetype/><d:getcontentlength/></d:prop></d:propfind>"""

        fun resolve(server: NetworkServer.WebDav, path: String): HttpUrl {
            val root = server.address.toHttpUrl()
            return root.newBuilder().apply {
                RemotePath.normalize(path).split('/').filter(String::isNotEmpty).forEach(::addPathSegment)
            }.build()
        }

        fun authorization(server: NetworkServer.WebDav): String? = server.credentials.let {
            if (it.username.isEmpty() && it.password.isEmpty()) null else Credentials.basic(it.username, it.password, Charsets.UTF_8)
        }

        fun parseListing(server: NetworkServer.WebDav, requestedUrl: HttpUrl, xml: String): List<NetworkEntry> {
            if (Regex("<!\\s*(?:DOCTYPE|ENTITY)", RegexOption.IGNORE_CASE).containsMatchIn(xml)) throw StorageException(StorageError.INVALID_RESPONSE)
            val document = try {
                DocumentBuilderFactory.newInstance().apply {
                    isNamespaceAware = true
                    isExpandEntityReferences = false
                }.newDocumentBuilder().apply {
                    setEntityResolver { _, _ -> InputSource(StringReader("")) }
                }.parse(InputSource(StringReader(xml)))
            } catch (error: Exception) { throw StorageException(StorageError.INVALID_RESPONSE, error) }
            val root = server.address.toHttpUrl()
            val rootSegments = root.pathSegments.filter(String::isNotEmpty)
            val currentPath = requestedUrl.encodedPath.trimEnd('/')
            val responses = document.getElementsByTagNameNS("DAV:", "response")
            val entries = LinkedHashMap<String, NetworkEntry>()
            for (index in 0 until responses.length) {
                val element = responses.item(index) as? Element ?: continue
                val href = element.text("href") ?: continue
                val url = requestedUrl.resolve(href) ?: continue
                if (url.scheme != root.scheme || url.host != root.host || url.port != root.port || url.query != null || url.fragment != null) continue
                if (url.encodedPath.trimEnd('/') == currentPath) continue
                val segments = url.pathSegments.filter(String::isNotEmpty)
                if (segments.take(rootSegments.size) != rootSegments) continue
                val relative = segments.drop(rootSegments.size)
                if (relative.any { '/' in it || '\\' in it || it == ".." }) continue
                val path = RemotePath.normalize(relative.joinToString("/"))
                if (RemotePath.parent(path) != RemotePath.normalize(requestedUrl.pathSegments.filter(String::isNotEmpty).drop(rootSegments.size).joinToString("/"))) continue
                val properties = element.getElementsByTagNameNS("DAV:", "propstat")
                for (propertyIndex in 0 until properties.length) {
                    val propstat = properties.item(propertyIndex) as? Element ?: continue
                    if (!propstat.text("status").orEmpty().matches(Regex("HTTP/\\S+ 2\\d\\d(?: .*)?"))) continue
                    val prop = propstat.getElementsByTagNameNS("DAV:", "prop").item(0) as? Element ?: continue
                    val directory = prop.getElementsByTagNameNS("DAV:", "collection").length > 0
                    val name = prop.text("displayname")?.takeIf(String::isNotBlank) ?: relative.lastOrNull() ?: continue
                    entries[path] = NetworkEntry(path, name, directory, prop.text("getcontentlength")?.toLongOrNull()?.coerceAtLeast(-1) ?: -1)
                    break
                }
            }
            return entries.values.sortedWith(compareBy<NetworkEntry> { !it.directory }.thenBy { it.name.lowercase() })
        }

        internal fun checkStatus(code: Int) {
            if (code in 200..299) return
            throw StorageException(when (code) {
                401, 403 -> StorageError.AUTHENTICATION
                404 -> StorageError.NOT_FOUND
                else -> StorageError.CONNECTION
            })
        }

        private fun Element.text(name: String): String? = getElementsByTagNameNS("DAV:", name).item(0)?.textContent?.trim()
    }
}

private suspend fun Call.await(): Response = suspendCancellableCoroutine { continuation ->
    continuation.invokeOnCancellation { cancel() }
    enqueue(object : Callback {
        override fun onFailure(call: Call, error: IOException) { if (continuation.isActive) continuation.resumeWithException(error) }
        override fun onResponse(call: Call, response: Response) {
            continuation.resume(response) { _, value, _ -> value.close() }
        }
    })
}

private class HttpRemoteFile(private val client: OkHttpClient, private val url: HttpUrl, private val authorization: String?) : RemoteFile {
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
            val request = Request.Builder().url(url).header("Range", "bytes=$position-")
                .apply { authorization?.let { header("Authorization", it) } }.build()
            val result = client.newCall(request).execute()
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
