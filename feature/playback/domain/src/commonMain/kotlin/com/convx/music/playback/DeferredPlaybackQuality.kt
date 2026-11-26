package com.convx.music.playback

/** A quality change applies immediately only when there is no active track. */
data class DeferredPlaybackQuality<T : Any>(val current: T, val pending: T? = null) {
    fun select(value: T, hasCurrentTrack: Boolean): DeferredPlaybackQuality<T> =
        if (!hasCurrentTrack) DeferredPlaybackQuality(value)
        else copy(pending = value.takeUnless { it == current })

    fun nextTrack(): DeferredPlaybackQuality<T> = DeferredPlaybackQuality(pending ?: current)

    /** The next item may be prepared before playback reports its transition. */
    fun forTrack(isCurrent: Boolean): T = if (isCurrent) current else pending ?: current
}

/** Immutable snapshot shared by source resolution and the player thread. */
data class TrackPlaybackQuality<T : Any>(
    val selection: DeferredPlaybackQuality<T>,
    val mediaId: String? = null,
    val fading: Map<String, T> = emptyMap(),
) {
    fun select(value: T, hasCurrentTrack: Boolean): TrackPlaybackQuality<T> =
        copy(selection = selection.select(value, hasCurrentTrack))

    fun transition(nextMediaId: String?, advance: Boolean): TrackPlaybackQuality<T> =
        copy(selection = if (nextMediaId == null || advance) selection.nextTrack() else selection, mediaId = nextMediaId)

    fun handoff(outgoingId: String?, incomingId: String): TrackPlaybackQuality<T> {
        val retain = outgoingId != null && outgoingId != incomingId
        // The loader identifies streams by media ID, so overlapping copies of the
        // same song must keep their existing rendition until they can be separated.
        return copy(selection = if (outgoingId == incomingId) selection else selection.nextTrack(), mediaId = incomingId,
            fading = if (retain) fading + (requireNotNull(outgoingId) to selection.current) else fading)
    }

    fun finishFade(outgoingId: String, quality: T): TrackPlaybackQuality<T> =
        if (fading[outgoingId] == quality) copy(fading = fading - outgoingId) else this

    fun forMedia(id: String): T = when {
        mediaId == id -> selection.current
        id in fading -> fading.getValue(id)
        mediaId == null -> selection.current
        else -> selection.forTrack(isCurrent = false)
    }
}
