/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.playback

import android.net.Uri
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.BaseDataSource
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import okhttp3.OkHttpClient
import okhttp3.Call
import okhttp3.Request
import java.io.File
import java.io.IOException
import java.io.InterruptedIOException
import java.net.URI

/**
 * Presents HLS as one cached file for offline playback. FLAC is remuxed; other codecs remain
 * fragmented MP4. Every segment is fetched into [spoolDir] before the first byte goes out, so the
 * file can start with a seek index (see [JoinedIndex]) that points at all of them.
 */
@UnstableApi
class HlsJoinDataSource(
    private val client: OkHttpClient,
    private val fallback: DataSource,
    private val spoolDir: File,
) : BaseDataSource(true) {
    private var active: DataSource? = null
    private var uri: Uri? = null
    private var spool: File? = null
    private var spoolInput: java.io.InputStream? = null
    private var parts: Iterator<() -> ByteArray> = emptyList<() -> ByteArray>().iterator()
    private var chunk = ByteArray(0)
    private var offset = 0
    private var skip = 0L
    private var remaining = C.LENGTH_UNSET.toLong()
    private var opened = false
    @Volatile private var cancelled = false
    @Volatile private var pendingCall: Call? = null

    override fun open(dataSpec: DataSpec): Long {
        cancelled = false
        uri = dataSpec.uri
        transferInitializing(dataSpec)
        if (dataSpec.uri.scheme != SCHEME) {
            active = fallback
            val length = fallback.open(dataSpec)
            transferStarted(dataSpec)
            opened = true
            return length
        }
        val playlist = mediaPlaylist(requireNotNull(dataSpec.uri.getQueryParameter("u")))
        val init = playlist.init?.let(::fetch)
        val flacHeader = init?.let(::flacHeader)
        val timescale = init?.let(JoinedIndex::timescale)
        val trackId = init?.let(JoinedIndex::trackId)
        val trexDuration = init?.let(JoinedIndex::defaultDuration)

        val file = File.createTempFile("join", ".part", spoolDir.apply { mkdirs() }).also { spool = it }
        val index = ArrayList<JoinedIndex.Segment>(playlist.segments.size)
        var indexable = timescale != null && timescale > 0
        file.outputStream().buffered().use { out ->
            for (url in playlist.segments) {
                val raw = fetch(url)
                val timing = if (indexable) JoinedIndex.timing(raw, trexDuration) else null
                if (timing == null) indexable = false
                val body = when {
                    flacHeader != null -> mdatPayloads(raw)
                    init != null -> JoinedIndex.withoutSidx(raw)
                    else -> raw
                }
                out.write(body)
                if (timing != null) index += JoinedIndex.Segment(body.size.toLong(), timing.first, timing.second)
            }
        }
        val head = when {
            flacHeader != null && indexable -> JoinedIndex.flacWithSeekTable(flacHeader, timescale!!, index)
            flacHeader != null -> flacHeader
            init != null && indexable && trackId != null -> init + JoinedIndex.sidx(trackId, timescale!!, index)
            else -> init ?: ByteArray(0)
        }
        val input = file.inputStream().buffered(SPOOL_CHUNK).also { spoolInput = it }
        val body = generateSequence<() -> ByteArray> {
            val buffer = ByteArray(SPOOL_CHUNK)
            val read = input.read(buffer)
            if (read <= 0) null else ({ if (read == buffer.size) buffer else buffer.copyOf(read) })
        }
        parts = (sequenceOf<() -> ByteArray>({ head }) + body).iterator()
        chunk = ByteArray(0)
        offset = 0
        // Joined byte offsets require replaying segments when resuming a download.
        skip = dataSpec.position
        remaining = dataSpec.length
        transferStarted(dataSpec)
        opened = true
        return C.LENGTH_UNSET.toLong()
    }

    override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
        if (length == 0) return 0
        active?.let {
            val read = it.read(buffer, offset, length)
            if (read > 0) bytesTransferred(read)
            return read
        }
        while (true) {
            if (cancelled || Thread.currentThread().isInterrupted) throw InterruptedIOException("HLS read cancelled")
            if (remaining == 0L) return C.RESULT_END_OF_INPUT
            if (this.offset == chunk.size) {
                if (!parts.hasNext()) return C.RESULT_END_OF_INPUT
                chunk = parts.next()()
                this.offset = 0
            }
            if (skip > 0) {
                val n = minOf(skip, (chunk.size - this.offset).toLong()).toInt()
                this.offset += n
                skip -= n
                continue
            }
            val n = minOf(length, chunk.size - this.offset)
                .let { if (remaining == C.LENGTH_UNSET.toLong()) it else minOf(it.toLong(), remaining).toInt() }
            if (n == 0) continue
            System.arraycopy(chunk, this.offset, buffer, offset, n)
            this.offset += n
            if (remaining != C.LENGTH_UNSET.toLong()) remaining -= n
            bytesTransferred(n)
            return n
        }
    }

    override fun getUri(): Uri? = uri

    override fun close() {
        cancelled = true
        pendingCall?.cancel()
        try {
            active?.close()
        } finally {
            active = null
            runCatching { spoolInput?.close() }
            spoolInput = null
            spool?.delete()
            spool = null
            parts = emptyList<() -> ByteArray>().iterator()
            chunk = ByteArray(0)
            offset = 0
            skip = 0
            uri = null
            if (opened) {
                opened = false
                transferEnded()
            }
        }
    }

    private class MediaPlaylist(val init: String?, val segments: List<String>)

    private fun mediaPlaylist(url: String, depth: Int = 0): MediaPlaylist {
        if (depth >= 8) throw IOException("HLS playlist nesting is too deep")
        val body = String(fetch(url))
        if (DashManifest.looksLikeMpd(body)) return DashManifest.parse(url, body).let { MediaPlaylist(it.init, it.segments) }
        val lines = body.lines().map(String::trim)
        // A master playlist: follow its first variant.
        lines.indexOfFirst { it.startsWith("#EXT-X-STREAM-INF") }.takeIf { it >= 0 }?.let { i ->
            return mediaPlaylist(resolve(url, lines.drop(i + 1).first { it.isNotEmpty() && !it.startsWith("#") }), depth + 1)
        }
        if (lines.any { it.startsWith("#EXT-X-KEY") && !it.contains("METHOD=NONE") }) throw IOException("Encrypted HLS")
        val init = lines.firstOrNull { it.startsWith("#EXT-X-MAP") }
            ?.substringAfter("URI=\"")?.substringBefore('"')?.let { resolve(url, it) }
        return MediaPlaylist(init, lines.filter { it.isNotEmpty() && !it.startsWith("#") }.map { resolve(url, it) })
    }

    private fun fetch(url: String): ByteArray {
        val call = client.newCall(Request.Builder().url(url).build())
        pendingCall = call
        return try {
            if (cancelled || Thread.currentThread().isInterrupted) throw InterruptedIOException("HLS fetch cancelled")
            call.execute().use { response ->
                if (!response.isSuccessful) throw IOException("HLS part HTTP ${response.code}")
                response.body.bytes()
            }
        } finally {
            if (pendingCall === call) pendingCall = null
        }
    }

    companion object {
        const val SCHEME = "hlsjoin"
        private const val SPOOL_CHUNK = 256 * 1024

        fun uriFor(playlistUrl: String): Uri =
            Uri.Builder().scheme(SCHEME).authority("x").appendQueryParameter("u", playlistUrl).build()

        private fun resolve(base: String, ref: String) = URI(base).resolve(ref).toString()
    }
}

/** (type, payload start, box end) for each box in [from, to). */
internal fun mp4Boxes(b: ByteArray, from: Int = 0, to: Int = b.size): Sequence<Triple<String, Int, Int>> = sequence {
    var o = from
    while (o + 8 <= to) {
        var size = u32(b, o)
        var header = 8
        if (size == 1L) {
            if (to - o < 16) break
            size = (u32(b, o + 8) shl 32) or u32(b, o + 12)
            header = 16
        }
        if (size == 0L) size = (to - o).toLong()
        if (size < header || size > (to - o).toLong()) break
        yield(Triple(String(b, o + 4, 4, Charsets.ISO_8859_1), o + header, (o + size).toInt()))
        o += size.toInt()
    }
}

private fun u32(b: ByteArray, o: Int): Long =
    ((b[o].toLong() and 0xff) shl 24) or ((b[o + 1].toLong() and 0xff) shl 16) or
        ((b[o + 2].toLong() and 0xff) shl 8) or (b[o + 3].toLong() and 0xff)

/** `fLaC` + the metadata blocks from the init segment's `dfLa` box; null when not FLAC. */
internal fun flacHeader(init: ByteArray): ByteArray? {
    // Container boxes to descend; stsd and the fLaC sample entry carry fixed fields first.
    val path = listOf("moov", "trak", "mdia", "minf", "stbl", "stsd", "fLaC", "dfLa")
    val skips = mapOf("stsd" to 8, "fLaC" to 28)
    var from = 0
    var to = init.size
    for (name in path) {
        val box = mp4Boxes(init, from, to).firstOrNull { it.first == name } ?: return null
        from = box.second + (skips[name] ?: 0)
        to = box.third
        if (from > to) return null
    }
    // dfLa is a FullBox: 4 bytes of version/flags, then the FLAC metadata blocks.
    if (to - from < 4) return null
    return "fLaC".toByteArray() + init.copyOfRange(from + 4, to)
}

/** Concatenated `mdat` payloads of one fMP4 media segment. */
internal fun mdatPayloads(segment: ByteArray): ByteArray {
    val payloads = mp4Boxes(segment).filter { it.first == "mdat" }.toList()
    val output = ByteArray(payloads.sumOf { it.third - it.second })
    var offset = 0
    for ((_, start, end) in payloads) {
        segment.copyInto(output, offset, start, end)
        offset += end - start
    }
    return output
}
