/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.playback

import android.net.Uri
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.HttpDataSource
import androidx.media3.datasource.TransferListener

/** Uses bounded ranges to avoid googlevideo's sustained-request throttling. */
@UnstableApi
class ChunkedDataSource(private val upstream: DataSource) : DataSource {

    class Factory(private val upstream: DataSource.Factory) : DataSource.Factory {
        override fun createDataSource() = ChunkedDataSource(upstream.createDataSource())
    }

    private var spec: DataSpec? = null
    private var chunked = false
    private var position = 0L
    private var remaining = C.LENGTH_UNSET.toLong()
    private var pieceLeft = 0L
    private var opened = false
    private var requestNumber = 0

    override fun addTransferListener(transferListener: TransferListener) =
        upstream.addTransferListener(transferListener)

    override fun open(dataSpec: DataSpec): Long {
        spec = dataSpec
        chunked = dataSpec.uri.isGoogleVideoUrl()
        if (!chunked) {
            opened = true
            return upstream.open(dataSpec)
        }
        position = dataSpec.position
        remaining = dataSpec.length
        val first = openPiece()
        val total = upstream.contentRangeLength()
        val available = when {
            total != null -> (total - dataSpec.position).coerceAtLeast(0L)
            first < CHUNK_BYTES -> first
            else -> C.LENGTH_UNSET.toLong()
        }
        if (available != C.LENGTH_UNSET.toLong()) {
            remaining = if (remaining == C.LENGTH_UNSET.toLong()) available else minOf(remaining, available)
        }
        return remaining
    }

    private fun openPiece(): Long {
        val wanted = if (remaining == C.LENGTH_UNSET.toLong()) CHUNK_BYTES else minOf(CHUNK_BYTES, remaining)
        val piece = requireNotNull(spec).buildUpon().setPosition(position).setLength(wanted).build()
        opened = true
        val length = retryOnce(
            retryable = { error -> error is HttpDataSource.InvalidResponseCodeException &&
                error.responseCode in setOf(403, 429, 500, 502, 503, 504) },
            pause = Thread::sleep,
        ) { attempt ->
            if (attempt > 0) upstream.close()
            upstream.open(piece.buildUpon().setUri(piece.uri.toString().withRequestNumber(requestNumber++)).build())
        }
        pieceLeft = if (length == C.LENGTH_UNSET.toLong()) wanted else length
        return pieceLeft
    }

    override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
        if (length == 0) return 0
        if (!chunked) return upstream.read(buffer, offset, length)
        while (true) {
            if (remaining == 0L) return C.RESULT_END_OF_INPUT
            if (!opened) {
                val next = try {
                    openPiece()
                } catch (e: HttpDataSource.InvalidResponseCodeException) {
                    if (e.responseCode == 416) return C.RESULT_END_OF_INPUT
                    throw e
                }
                if (next == 0L) return C.RESULT_END_OF_INPUT
            }
            val read = upstream.read(buffer, offset, minOf(length.toLong(), pieceLeft).toInt())
            if (read == C.RESULT_END_OF_INPUT) {
                if (pieceLeft > 0) return C.RESULT_END_OF_INPUT
                upstream.close()
                opened = false
                continue
            }
            position += read
            pieceLeft -= read
            if (remaining != C.LENGTH_UNSET.toLong()) remaining -= read
            if (pieceLeft == 0L) {
                upstream.close()
                opened = false
            }
            return read
        }
    }

    override fun getUri(): Uri? = upstream.uri ?: spec?.uri

    override fun getResponseHeaders(): Map<String, List<String>> = upstream.responseHeaders

    override fun close() {
        try {
            if (opened) upstream.close()
        } finally {
            opened = false
            spec = null
            pieceLeft = 0
            remaining = C.LENGTH_UNSET.toLong()
        }
    }

    private companion object {
        const val CHUNK_BYTES = 512L * 1024
    }
}

internal fun Uri.isGoogleVideoUrl(): Boolean =
    scheme == "https" && (host == "googlevideo.com" || host.orEmpty().endsWith(".googlevideo.com"))

@UnstableApi
internal fun DataSource.contentRangeLength(): Long? = responseHeaders.entries
    .firstOrNull { it.key.equals("Content-Range", ignoreCase = true) }
    ?.value?.firstOrNull()?.substringAfterLast('/', "")?.trim()?.toLongOrNull()
