package com.convx.music.playback.presentation

import com.convx.music.lyrics.LyricsQuery
import com.convx.music.lyrics.LyricsDocument
import com.convx.music.lyrics.SharedLyricsSource
import com.convx.music.lyrics.bitchord.LyricLine
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class LyricsScreenState(
    val query: LyricsQuery? = null,
    val document: LyricsDocument? = null,
    val selectedSource: String? = null,
    val isLoading: Boolean = false,
    val message: String? = null,
)

/** Owns provider selection, cancellation, bounded lookup, and a small session cache. */
class LyricsStateHolder(
    val sources: List<SharedLyricsSource>,
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
    private val workDispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)
    private val mutableState = MutableStateFlow(LyricsScreenState())
    val state = mutableState.asStateFlow()
    private val cache = linkedMapOf<Pair<LyricsQuery, String?>, LyricsDocument>()
    private var request: Job? = null
    private var generation = 0L

    fun load(query: LyricsQuery?, sourceId: String? = mutableState.value.selectedSource, refresh: Boolean = false) {
        request?.cancel()
        val requestGeneration = ++generation
        val selected = sourceId?.takeIf { id -> sources.any { it.id == id } }
        val cached = if (refresh || query == null) null else cache[query to selected]
        mutableState.value = LyricsScreenState(query, cached, selected, isLoading = query != null && cached == null)
        if (query == null || cached != null) return
        request = scope.launch {
            val candidates = if (selected == null) sources else sources.filter { it.id == selected }
            var best: LyricsDocument? = null
            for (batch in candidates.chunked(3)) {
                val documents = withContext(workDispatcher) {
                    coroutineScope {
                        batch.map { source ->
                            async {
                                try {
                                    source.lyrics(query)?.takeIf { lines -> lines.any { it.text.isNotBlank() } }
                                        ?.let { LyricsDocument(source.id, source.label, it.sortedBy(LyricLine::timeMs)) }
                                } catch (cancelled: CancellationException) {
                                    throw cancelled
                                } catch (_: Exception) {
                                    null
                                }
                            }
                        }.awaitAll().filterNotNull()
                    }
                }
                if (generation != requestGeneration) return@launch
                for (document in documents) {
                    if (best == null || quality(document) > quality(requireNotNull(best))) best = document
                }
                best?.let { mutableState.value = mutableState.value.copy(document = it) }
                if (best?.hasWordTimings == true || selected != null) break
            }
            if (generation != requestGeneration) return@launch
            best?.let {
                cache[query to selected] = it
                if (cache.size > 32) cache.remove(cache.keys.first())
            }
            mutableState.value = mutableState.value.copy(
                isLoading = false,
                document = best,
                message = if (best == null) "No lyrics found. Try another provider." else null,
            )
        }
    }

    fun selectSource(sourceId: String?) = load(mutableState.value.query, sourceId)
    fun retry() = load(mutableState.value.query, refresh = true)
    fun close() { request?.cancel(); scope.cancel(); cache.clear() }

    private fun quality(document: LyricsDocument): Int = when {
        document.hasWordTimings -> 2
        document.isSynced -> 1
        else -> 0
    }
}
