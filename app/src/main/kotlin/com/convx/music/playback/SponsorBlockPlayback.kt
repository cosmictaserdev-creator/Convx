/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
package com.convx.music.playback

import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.Player
import com.convx.music.extensions.currentMetadata
import com.convx.music.playback.data.JvmSponsorSegmentsSource

/** Only Media3 state and commands stay in Android; requests and skip policy are shared. */
internal class SponsorBlockPlayback(
    private val player: () -> ExoPlayer,
    private val pauseAtSongEnd: () -> Boolean,
) {
    private val session = SponsorBlockSession(JvmSponsorSegmentsSource())
    private var previousPlayer: ExoPlayer? = null
    private var previousItem: Pair<String?, Int>? = null
    private var generation = 0L

    private fun readState(): SponsorPlaybackState {
        val active = player()
        val item = active.currentMediaItem?.mediaId to active.currentMediaItemIndex
        if (active !== previousPlayer || item != previousItem) {
            previousPlayer = active
            previousItem = item
            generation++
        }
        return SponsorPlaybackState(
            videoId = item.first.takeUnless { active.currentMetadata?.isLocalFile == true },
            positionMs = active.currentPosition,
            durationMs = active.duration.takeIf { it > 0 },
            isPlaying = active.isPlaying,
            canSkipNext = active.repeatMode != Player.REPEAT_MODE_ONE && active.hasNextMediaItem() && !pauseAtSongEnd(),
            generation = generation,
        )
    }

    suspend fun run(categories: Set<SponsorCategory>) = session.run(::readState, { expected, action ->
        val current = readState()
        if (current.videoId == expected.videoId && current.generation == expected.generation && current.isPlaying) {
            when (action) {
                is SponsorSkipAction.Seek -> player().seekTo(action.positionMs)
                SponsorSkipAction.NextTrack -> if (current.canSkipNext) player().seekToNextMediaItem()
            }
        }
    }, categories)
}
