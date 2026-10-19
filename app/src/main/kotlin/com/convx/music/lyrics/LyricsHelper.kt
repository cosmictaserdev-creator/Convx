/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.lyrics

import android.content.Context
import android.util.LruCache
import com.convx.music.constants.LyricsProviderOrderKey
import com.convx.music.constants.PreferredLyricsProvider
import com.convx.music.constants.PreferredLyricsProviderKey
import com.convx.music.db.entities.LyricsEntity.Companion.LYRICS_NOT_FOUND
import com.convx.music.extensions.toEnum
import com.convx.music.models.MediaMetadata
import com.convx.music.utils.NetworkConnectivityObserver
import com.convx.music.utils.dataStore
import com.convx.music.utils.reportException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

class LyricsHelper
@Inject
constructor(
    @ApplicationContext private val context: Context,
    private val networkConnectivity: NetworkConnectivityObserver,
    private val providerRepository: LyricsProviderRepository,
) {
    /**
     * Resolves the ordered list of lyrics providers from the user's saved priority order.
     * Falls back to migrating the legacy [PreferredLyricsProvider] enum if the new order
     * preference has not been written yet, ensuring a smooth upgrade for existing users.
     */
    private suspend fun resolveLyricsProviders(): List<LyricsProvider> {
        val preferences = context.dataStore.data.first()
        val orderString = preferences[LyricsProviderOrderKey].orEmpty()

        if (orderString.isNotBlank()) {
            return LyricsProviderRegistry.getOrderedProviders(orderString)
        }

        // Use the legacy provider only when the user explicitly saved it; an unset key must keep
        // the LrcLib-first default instead of putting the slower YouLyPlus chain first.
        val preferredEnum = preferences[PreferredLyricsProviderKey]
            ?.let { it.toEnum(PreferredLyricsProvider.YOULYPLUS) }
            ?.let(LyricsProviderSettings::idForLegacyPreference)
            ?: return LyricsProviderRegistry.getDefaultProviderOrder()
                .mapNotNull(LyricsProviderRegistry::getProvider)

        val defaultOrder = LyricsProviderRegistry.getDefaultProviderOrder()
        val migratedOrder = listOf(preferredEnum) + defaultOrder.filter { it != preferredEnum }
        return migratedOrder.mapNotNull(LyricsProviderRegistry::getProvider)
    }

    private val cache = LruCache<String, List<LyricsResult>>(MAX_CACHE_SIZE)
    private var currentLyricsJob: Job? = null

    suspend fun getLyrics(
        mediaMetadata: MediaMetadata,
        initialLyrics: LyricsWithProvider? = null,
        onUpgrade: suspend (LyricsWithProvider) -> Unit = {},
    ): LyricsWithProvider {
        currentLyricsJob?.cancel()

        if (!hasNetworkConnection()) {
            return LyricsWithProvider(LYRICS_NOT_FOUND, "Unknown")
        }

        val providers = resolveLyricsProviders().usableFor(isLocalSong(mediaMetadata.id))
            .filter { LyricsProviderSettings.isEnabled(context, it) }
        return coroutineScope {
            val policy = LyricsUpgradePolicy()
            initialLyrics?.takeIf { initial -> providers.any { it.id == initial.provider } }?.let {
                policy.consider(it.provider, providers.indexOfFirst { p -> p.id == it.provider }, it.lyrics)
            }
            val remaining = providers.indices.toMutableList()
            // Keep one fast line fallback in the first window while the leading
            // word providers work. No more than three source attempts run at once.
            val fallback = providers.indexOfFirst { it.id == "LrcLib" }
            if (fallback > 2) { remaining.remove(fallback); remaining.add(2, fallback) }
            val answers = Channel<Pair<Int, Result<String>>>(Channel.UNLIMITED)
            val jobs = mutableListOf<Job>()
            val completed = hashSetOf<Int>()
            var inFlight = 0
            fun fillWindow() {
                while (inFlight < 3 && remaining.isNotEmpty()) {
                    val bestWord = policy.best?.takeIf { it.sync == LyricsSyncLevel.WORD }
                    val next = if (bestWord == null) 0 else remaining.indexOfFirst { it < bestWord.priority }
                    if (next < 0) break
                    val index = remaining.removeAt(next)
                    inFlight++
                    jobs += launch(Dispatchers.IO) {
                        val result = providerRepository.fetch(providers[index], mediaMetadata)
                        answers.send(index to result)
                    }
                }
            }
            val deadline = System.nanoTime() / 1_000_000 + 20_000L
            try {
                fillWindow()
                while (inFlight > 0) {
                    val timeout = deadline - System.nanoTime() / 1_000_000
                    if (timeout <= 0) break
                    val answer = withTimeoutOrNull(timeout) { answers.receive() } ?: break
                    val (index, result) = answer
                    inFlight--
                    completed += index
                    val provider = providers[index]
                    result.getOrNull()?.takeIf { it.isNotBlank() && it != LYRICS_NOT_FOUND && LyricsProviderSettings.isEnabled(context, provider) }
                        ?.let { policy.consider(provider.id, index, it) }
                        ?.let { onUpgrade(LyricsWithProvider(it.lyrics, it.provider)) }
                    val best = policy.best
                    if (best?.sync == LyricsSyncLevel.WORD && (0 until best.priority).all { it in completed }) break
                    fillWindow()
                }
                policy.best?.let { LyricsWithProvider(it.lyrics, it.provider) }
                    ?: LyricsWithProvider(LYRICS_NOT_FOUND, "Unknown")
            } finally {
                jobs.forEach { it.cancel() }
                answers.cancel()
            }
        }
    }

    suspend fun getAllLyrics(
        mediaId: String,
        songTitle: String,
        songArtists: String,
        duration: Int,
        album: String? = null,
        callback: (LyricsResult) -> Unit,
    ) {
        currentLyricsJob?.cancel()

        val providers = resolveLyricsProviders().usableFor(isLocalSong(mediaId))
            .filter { LyricsProviderSettings.isEnabled(context, it) }
        // Priority and enabled state are part of the cache identity. Otherwise a
        // previously cached result silently ignores a changed provider order.
        val cacheKey = listOf(mediaId, songArtists, songTitle, duration.toString(), album.orEmpty(),
            providers.joinToString(",") { it.id }).joinToString("|")
        cache.get(cacheKey)?.let { results ->
            results.forEach {
                callback(it)
            }
            return
        }

        if (!hasNetworkConnection()) return

        val allResult = mutableListOf<LyricsResult>()
        currentLyricsJob = CoroutineScope(SupervisorJob()).launch {
            providers.forEach { provider ->
                    try {
                        LyricsProviderSettings.initialize(context, provider)
                        withTimeoutOrNull(10_000) {
                            provider.getAllLyrics(mediaId, songTitle, songArtists, duration, album) { lyrics ->
                                val result = LyricsResult(provider.id, provider.displayName, lyrics)
                                allResult += result
                                callback(result)
                            }
                        }
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        reportException(e)
                    }
            }
            cache.put(cacheKey, allResult)
        }

        currentLyricsJob?.join()
    }

    fun cancelCurrentLyricsJob() {
        currentLyricsJob?.cancel()
        currentLyricsJob = null
    }

    /** MediaStore content IDs cannot be used by providers that require a YouTube video ID. */
    private fun isLocalSong(id: String): Boolean = id.startsWith("content://")

    private fun hasNetworkConnection(): Boolean = try {
        networkConnectivity.isCurrentlyConnected()
    } catch (_: Exception) {
        // Proceed when the connectivity probe itself fails.
        true
    }

    private fun List<LyricsProvider>.usableFor(isLocalSong: Boolean): List<LyricsProvider> =
        if (!isLocalSong) this else filterNot(LyricsProvider::requiresVideoId)

    companion object {
        private const val MAX_CACHE_SIZE = 3
    }
}

data class LyricsResult(
    val providerId: String,
    val providerName: String,
    val lyrics: String,
)

data class LyricsWithProvider(
    val lyrics: String,
    val provider: String,
)
