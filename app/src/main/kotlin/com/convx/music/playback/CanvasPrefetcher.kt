package com.convx.music.playback

import android.net.Uri
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.cache.Cache
import com.convx.music.canvas.canvasCacheKey
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

/** Reads a short progressive canvas through the same bounded cache used by playback. */
@UnstableApi
internal class CanvasPrefetcher(
    private val playback: Cache,
    private val downloads: Cache,
    private val factory: DataSource.Factory,
) {
    suspend fun fill(mediaId: String, url: String, canContinue: suspend () -> Boolean): Boolean {
        val key = canvasCacheKey(mediaId, url)
        if (downloads.isCachedToEnd(key, 0) || playback.isCachedToEnd(key, 0)) return true
        if (!canContinue()) return false
        val source = factory.createDataSource()
        var complete = false
        try {
            source.open(DataSpec.Builder().setUri(Uri.parse(url)).setKey(key)
                .setFlags(DataSpec.FLAG_ALLOW_CACHE_FRAGMENTATION).build())
            val buffer = ByteArray(64 * 1024)
            var sinceCheck = 0
            while (true) {
                currentCoroutineContext().ensureActive()
                if (sinceCheck >= 256 * 1024) {
                    if (!canContinue()) return false
                    sinceCheck = 0
                }
                val count = source.read(buffer, 0, buffer.size)
                if (count == C.RESULT_END_OF_INPUT) { complete = true; break }
                if (count == 0) throw java.io.IOException("Empty canvas read")
                sinceCheck += count
            }
        } catch (error: java.io.IOException) {
            if (error.isCacheBudgetExhausted()) return false
            throw error
        } finally {
            source.close()
        }
        // A budgeted sink may stop admitting data while upstream keeps serving it.
        return complete && (downloads.isCachedToEnd(key, 0) || playback.isCachedToEnd(key, 0))
    }
}
