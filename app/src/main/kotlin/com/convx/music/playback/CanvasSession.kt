package com.convx.music.playback

internal data class CanvasTrack(val mediaId: String?, val epoch: Long) {
    companion object {
        private val nextEpoch = java.util.concurrent.atomic.AtomicLong()

        fun transition(mediaId: String?): CanvasTrack = CanvasTrack(mediaId, nextEpoch.incrementAndGet())
    }
}

internal enum class CanvasPhase { Thumbnail, Resolving, Found, Showing, Disabled }

internal data class CanvasSession(
    val track: CanvasTrack,
    val phase: CanvasPhase = CanvasPhase.Thumbnail,
) {
    fun resolve(track: CanvasTrack, enabled: Boolean) = CanvasSession(
        track,
        when {
            !enabled -> CanvasPhase.Disabled
            track.mediaId == null -> CanvasPhase.Thumbnail
            else -> CanvasPhase.Resolving
        },
    )

    fun resolved(track: CanvasTrack, found: Boolean): CanvasSession {
        if (this.track != track || phase != CanvasPhase.Resolving) return this
        return copy(phase = if (found) CanvasPhase.Found else CanvasPhase.Thumbnail)
    }

    fun firstFrame(track: CanvasTrack): CanvasSession {
        if (this.track != track || phase != CanvasPhase.Found) return this
        return copy(phase = CanvasPhase.Showing)
    }
}
