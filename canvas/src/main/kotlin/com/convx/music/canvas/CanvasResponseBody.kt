package com.convx.music.canvas

import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.charset
import io.ktor.utils.io.readAvailable
import java.io.ByteArrayOutputStream
import java.io.IOException

private const val MAX_JSON_BYTES = 1024 * 1024

/** Reads at most [maxBytes] of decoded body text inside a streaming HTTP statement. */
suspend fun HttpResponse.readCanvasResponseText(maxBytes: Int = MAX_JSON_BYTES): String {
    require(maxBytes > 0)
    val channel = bodyAsChannel()
    val buffer = ByteArray(minOf(8192, maxBytes))
    val output = ByteArrayOutputStream(buffer.size)
    var remaining = maxBytes

    while (remaining > 0) {
        val count = channel.readAvailable(buffer, 0, minOf(buffer.size, remaining))
        if (count < 0) break
        output.write(buffer, 0, count)
        remaining -= count
    }
    if (remaining == 0 && channel.readAvailable(buffer, 0, 1) >= 0) {
        throw IOException("Canvas provider response exceeded its size limit")
    }
    channel.closedCause?.let { throw it }
    return output.toString((charset() ?: Charsets.UTF_8).name())
}
