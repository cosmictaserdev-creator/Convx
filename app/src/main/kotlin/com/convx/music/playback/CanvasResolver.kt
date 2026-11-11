/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.playback

import android.content.Context
import com.convx.music.applecanvas.AppleMusicCanvasProvider
import com.convx.music.canvas.CanvasArtwork
import com.convx.music.canvas.TidalCanvasProvider
import com.convx.music.constants.CanvasSource
import com.convx.music.constants.CanvasSourceKey
import com.convx.music.constants.CanvasSourceOrderKey
import com.convx.music.convxcanvas.ConvxCanvasProvider
import com.convx.music.db.MusicDatabase
import com.convx.music.db.entities.CanvasDecisionEntity
import com.convx.music.models.MediaMetadata
import com.convx.music.utils.SpotifyCanvasLookup
import com.convx.music.utils.dataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.Locale

data class CanvasDecision(
    val mediaId: String,
    /** Null in a negative decision: the ladder ran and found no canvas. */
    val source: CanvasSource?,
    val primaryUrl: String?,
    val fallbackUrl: String?,
)

/**
 * Single owner of per-track canvas decisions. Every surface — the cover, the queue
 * header, mini bars, the classic thumbnail — reads the same decision instead of
 * climbing the ladder on its own.
 */
class CanvasResolver(
    private val context: Context,
    private val database: MusicDatabase,
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    /** In-memory mirror of [CanvasDecisionEntity] rows; populated on first read per track. */
    private val decisions = MutableStateFlow<Map<String, CanvasDecision>>(emptyMap())

    /** Serializes resolves; also throttles stampedes, matching the staggered prefetch design. */
    private val resolveMutex = Mutex()
    private var migrationDone = false

    /**
     * The player-canvas ladder order set in Canvas Selection, persisted as the successor
     * of the old auto/single-source preference. Ambient canvas keeps [CanvasSourceKey] alone.
     */
    private val sourceOrder = MutableStateFlow(CanvasSource.DEFAULT_ORDER)

    val sourceOrderState: StateFlow<List<CanvasSource>> get() = sourceOrder.asStateFlow()

    init {
        context.dataStore
            .data
            .map { prefs ->
                prefs[CanvasSourceOrderKey]?.let { encoded ->
                    encoded.split(ORDER_SEPARATOR).mapNotNull(CanvasSource.Companion::fromName).ifEmpty { CanvasSource.DEFAULT_ORDER }
                }
                    ?: prefs[CanvasSourceKey]?.let { legacy ->
                        CanvasSource.entries.firstOrNull { it.name == legacy }?.let { listOf(it) }
                    }
                    ?: CanvasSource.DEFAULT_ORDER
            }
            .onEach { order ->
                sourceOrder.value = order
                context.dataStore.data.firstOrNull() // warm the mirror; see below
            }
            .launchIn(scope)
    }

    fun stateFor(mediaId: String?): Flow<CanvasDecision?> = decisions.map { it[mediaId] }

    /** Full decision path: memory, then the persisted row, then the ladder. */
    suspend fun resolve(metadata: MediaMetadata): CanvasDecision? {
        val id = metadata.id ?: return null
        decisions.value[id]?.let { return it }
        return resolveMutex.withLock {
            decisions.value[id]
                ?: withContext(Dispatchers.IO) {
                    val order = sourceOrder.value
                    val resolved = resolveLadder(metadata, order)
                    store(id, resolved)
                    resolved
                }
        }
    }

    /** Playback hit an expired/rotten url: drop the row so the next resolve re-climbs. */
    suspend fun invalidate(mediaId: String) {
        decisions.update { it - mediaId }
        withContext(Dispatchers.IO) {
            database.invalidateCanvasDecision(mediaId)
        }
    }

    /** Preserve positive rows, forget negatives: an order change never rotates urls, only which source wins. */
    suspend fun onSourceOrderChanged() {
        decisions.update { current ->
            current.filterValues { decision -> decision.source != null }
        }
        withContext(Dispatchers.IO) {
            database.invalidateNegativeCanvasDecisions()
        }
    }

    private suspend fun resolveLadder(metadata: MediaMetadata, order: List<CanvasSource>): CanvasDecision? {
        val id = metadata.id ?: return null
        database.canvasDecision(id)?.toDecision()?.let { return it }
        return runLadder(metadata, order).also { store(id, it) }
    }

    private fun store(id: String, decision: CanvasDecision?) {
        // Negative resolves are remembered too, so the session never re-climbs a ladder that already missed.
        decisions.update { it + (id to decision) }
    }

    private suspend fun runLadder(metadata: MediaMetadata, order: List<CanvasSource>): CanvasDecision? {
        val id = metadata.id ?: return null
        val winner = runCatching {
            withContext(Dispatchers.IO) {
                val songTitle = metadata.title
                val artistName = metadata.artists.firstOrNull()?.name ?: ""
                val albumName = metadata.album?.title ?: ""
                val countryCode = Locale.getDefault().country
                val storefront = if (countryCode.length == 2) countryCode.lowercase(Locale.ROOT) else "us"
                var found: Pair<CanvasSource, CanvasArtwork>? = null
                for (source in order) {
                    val artwork = runCatching {
                        when (source) {
                            CanvasSource.SPOTIFY -> SpotifyCanvasLookup.getBySongArtist(context, songTitle, artistName)
                            CanvasSource.APPLE_MUSIC ->
                                AppleMusicCanvasProvider.getBySongArtist(songTitle, artistName, albumName, storefront)
                                    ?.takeIf { !it.animated.isNullOrBlank() }
                            CanvasSource.TIDAL ->
                                TidalCanvasProvider.getBySongArtist(songTitle, artistName, albumName)
                                    ?.takeIf { !it.animated.isNullOrBlank() }
                            CanvasSource.CONVX ->
                                ConvxCanvasProvider.getBySongArtist(songTitle, artistName)
                                    ?.takeIf { !it.animated.isNullOrBlank() }
                        }
                    }.getOrNull()
                    if (artwork != null) {
                        found = source to artwork
                        break
                    }
                }
                found
            }
        }.getOrNull()
        val decision = winner?.let { (source, artwork) ->
            CanvasDecision(
                mediaId = id,
                source = source,
                primaryUrl = artwork.preferredAnimationUrl?.takeIf { it.isNotBlank() },
                fallbackUrl = artwork.animated?.takeIf { it.isNotBlank() },
            )
        }
        persist(id, decision)
        return decision
    }

    private suspend fun persist(id: String, found: CanvasDecision?) {
        withContext(Dispatchers.IO) {
            val local = found ?: CanvasDecision(id, null, null, null)
            database.upsertCanvasDecisions(listOf(local.toEntity()))
        }
    }
}

private const val ORDER_SEPARATOR = ","

fun CanvasDecision.toEntity(): CanvasDecisionEntity =
    CanvasDecisionEntity(
        id = mediaId,
        source = source?.name ?: CanvasDecisionEntity.NO_CANVAS,
        primaryUrl = primaryUrl,
        fallbackUrl = fallbackUrl,
        updatedAt = System.currentTimeMillis(),
    )

fun CanvasDecisionEntity.toDecision(): CanvasDecision =
    if (isFound && !primaryUrl.isNullOrBlank()) {
        CanvasDecision(
            mediaId = id,
            source = CanvasSource.entries.firstOrNull { it.name == source },
            primaryUrl = primaryUrl,
            fallbackUrl = fallbackUrl,
        )
    } else {
        CanvasDecision(
            mediaId = id,
            source = null,
            primaryUrl = null,
            fallbackUrl = null,
        )
    }
