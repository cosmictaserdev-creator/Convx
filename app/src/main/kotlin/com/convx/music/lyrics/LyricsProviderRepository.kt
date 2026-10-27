package com.convx.music.lyrics

import android.content.Context
import com.convx.music.db.entities.LyricsEntity
import com.convx.music.db.entities.LyricsEntity.Companion.LYRICS_NOT_FOUND
import com.convx.music.models.MediaMetadata
import com.convx.music.utils.dataStore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withTimeout

enum class LyricsProviderStatus { NOT_FETCHED, FETCHING, FOUND, NOT_FOUND, FAILED, DISABLED, UNSUPPORTED }

/** Shared by automatic lookup and the lyrics page, so choosing a source reuses its request. */
class LyricsProviderRepository(private val context: Context) {
    private data class SongKey(val id: String, val title: String, val artist: String, val duration: Int, val album: String?)
    private data class Key(val song: SongKey, val provider: String)
    private class Attempt(val work: Deferred<Result<String>>) {
        var status = LyricsProviderStatus.FETCHING
        var completedAt = 0L
        var syncLevel = LyricsSyncLevel.PLAIN
    }

    private val lock = Any()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val gate = Semaphore(3)
    private val attempts = LinkedHashMap<Key, Attempt>(40, 0.75f, true)
    private val revision = MutableStateFlow(0L)

    private fun MediaMetadata.key() = SongKey(id, title, artists.joinToString { it.name }, duration, album?.title)

    fun statuses(song: MediaMetadata): Flow<Map<String, LyricsProviderStatus>> = combine(revision, context.dataStore.data) { _, preferences ->
        val identity = song.key()
        LyricsProviderRegistry.providerIds.associateWith { id ->
            val provider = LyricsProviderRegistry.getProvider(id)
            when {
                identity.id.startsWith("content://") && provider?.requiresVideoId == true -> LyricsProviderStatus.UNSUPPORTED
                provider == null || preferences[LyricsProviderSettings.enabledKey(id)] == false -> LyricsProviderStatus.DISABLED
                else -> synchronized(lock) {
                    attempts[Key(identity, provider.id)]?.status ?: LyricsProviderStatus.NOT_FETCHED
                }
            }
        }
    }.flowOn(Dispatchers.IO)

    fun syncLevels(song: MediaMetadata): Flow<Map<String, LyricsSyncLevel>> = revision.map {
        synchronized(lock) {
            LyricsProviderRegistry.providerIds.associateWith { attempts[Key(song.key(), it)]?.syncLevel ?: LyricsSyncLevel.PLAIN }
        }
    }.flowOn(Dispatchers.IO)

    fun remember(song: MediaMetadata, lyrics: LyricsEntity?) {
        if (lyrics == null || lyrics.id != song.id || lyrics.lyrics.isBlank() || lyrics.lyrics == LYRICS_NOT_FOUND) return
        val provider = LyricsProviderRegistry.getProviderForStoredName(lyrics.provider) ?: return
        synchronized(lock) {
            val key = Key(song.key(), provider.id)
            if (attempts[key]?.status in setOf(LyricsProviderStatus.FETCHING, LyricsProviderStatus.FOUND)) return
            trim()
            attempts[key] = Attempt(CompletableDeferred(Result.success(lyrics.lyrics))).apply {
                status = LyricsProviderStatus.FOUND
                syncLevel = LyricsSyncLevel.detect(lyrics.lyrics)
                completedAt = now()
            }
            revision.value++
        }
    }

    suspend fun fetch(provider: LyricsProvider, song: MediaMetadata): Result<String> = fetch(
        provider, song.id, song.title, song.artists.joinToString { it.name }, song.duration, song.album?.title,
    )

    suspend fun fetch(
        provider: LyricsProvider, id: String, title: String, artist: String, duration: Int, album: String?,
    ): Result<String> {
        if (!LyricsProviderSettings.isEnabled(context, provider) ||
            (id.startsWith("content://") && provider.requiresVideoId)
        ) {
            return Result.failure(IllegalStateException("Lyrics provider is unavailable"))
        }
        val key = Key(SongKey(id, title, artist, duration, album), provider.id)
        val attempt = synchronized(lock) {
            val existing = attempts[key]
            val ttl = if (existing?.status == LyricsProviderStatus.NOT_FOUND) 120_000L else 600_000L
            if (existing != null && (existing.work.isActive ||
                    (existing.status != LyricsProviderStatus.FAILED && now() - existing.completedAt < ttl))) {
                existing
            } else {
                trim()
                lateinit var pending: Attempt
                val work = scope.async(start = CoroutineStart.LAZY) {
                    var status = LyricsProviderStatus.FAILED
                    val result = try {
                        gate.withPermit {
                            if (!LyricsProviderSettings.isEnabled(context, provider)) {
                                return@withPermit Result.failure<String>(IllegalStateException("Lyrics provider is disabled"))
                            }
                            withTimeout(12_000L) {
                                LyricsProviderSettings.initialize(context, provider)
                                provider.getLyrics(id, title, artist, duration, album).also { result ->
                                    status = if (result.isSuccess) {
                                        if (result.getOrNull().isNullOrBlank() || result.getOrNull() == LYRICS_NOT_FOUND)
                                            LyricsProviderStatus.NOT_FOUND else LyricsProviderStatus.FOUND
                                    } else {
                                        val error = result.exceptionOrNull()
                                        if (error is NoSuchElementException || error?.message?.let {
                                            it.contains("not found", true) || it.contains("no lyrics", true)
                                        } == true) LyricsProviderStatus.NOT_FOUND else LyricsProviderStatus.FAILED
                                    }
                                }
                            }
                        }
                    } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
                        Result.failure(e)
                    } catch (e: CancellationException) {
                        synchronized(lock) { attempts.remove(key); revision.value++ }
                        throw e
                    } catch (e: Exception) {
                        Result.failure(e)
                    }
                    val syncLevel = result.getOrNull()?.let(LyricsSyncLevel::detect) ?: LyricsSyncLevel.PLAIN
                    synchronized(lock) {
                        pending.status = status
                        pending.syncLevel = syncLevel
                        pending.completedAt = now()
                        revision.value++
                    }
                    if (status == LyricsProviderStatus.NOT_FOUND) Result.failure(NoSuchElementException("No lyrics"))
                    else result
                }
                pending = Attempt(work)
                attempts[key] = pending
                revision.value++
                work.start()
                pending
            }
        }
        return attempt.work.await()
    }

    private fun trim() {
        while (attempts.size >= 40) {
            val oldest = attempts.entries.firstOrNull { !it.value.work.isActive } ?: break
            attempts.remove(oldest.key)
        }
    }

    private fun now() = System.nanoTime() / 1_000_000
}
