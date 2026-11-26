package com.convx.music.playback

import android.content.Context
import com.convx.music.constants.CanvasSource
import com.convx.music.constants.CanvasProviderOrderKey
import com.convx.music.db.MusicDatabase
import com.convx.music.db.entities.CanvasDecisionEntity
import com.convx.music.models.MediaMetadata
import com.convx.music.utils.dataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*

data class CanvasDecision(
    val mediaId: String,
    val source: CanvasSource?,
    val primaryUrl: String?,
    val fallbackUrl: String?,
)

/** Compatibility host for the shared selection policy and existing persistent decision store. */
class CanvasResolver(
    private val context: Context,
    @Suppress("UNUSED_PARAMETER") database: MusicDatabase,
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val decisions = MutableStateFlow<Map<String, CanvasDecision>>(emptyMap())
    private val defaultOrder = listOf(CanvasSource.SPOTIFY, CanvasSource.APPLE_MUSIC, CanvasSource.TIDAL, CanvasSource.CONVX)
    val sourceOrderState: StateFlow<List<CanvasSource>> = context.dataStore.data.map { preferences ->
        val saved = preferences[CanvasProviderOrderKey].orEmpty().split(',').mapNotNull { name ->
            defaultOrder.firstOrNull { it.name == name }
        }.distinct()
        saved + defaultOrder.filterNot(saved::contains)
    }.stateIn(scope, SharingStarted.Eagerly, defaultOrder)

    fun stateFor(mediaId: String?): Flow<CanvasDecision?> = decisions.map { it[mediaId] }

    suspend fun resolve(metadata: MediaMetadata): CanvasDecision {
        val artwork = CanvasSelectionRepository.resolve(context, metadata.id, metadata.title,
            metadata.artists.joinToString { it.name }, metadata.album?.title, CanvasSource.AUTO)
        val decision = CanvasDecision(metadata.id, if (artwork == null) null else CanvasSource.AUTO,
            artwork?.preferredAnimationUrl, artwork?.animated)
        decisions.update { it + (metadata.id to decision) }
        return decision
    }

    suspend fun invalidate(mediaId: String) {
        decisions.update { it - mediaId }
        CanvasSelectionRepository.invalidate(context, mediaId)
    }

    suspend fun onSourceOrderChanged() {
        decisions.value.filterValues { it.primaryUrl == null }.keys.forEach { invalidate(it) }
    }
}

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
