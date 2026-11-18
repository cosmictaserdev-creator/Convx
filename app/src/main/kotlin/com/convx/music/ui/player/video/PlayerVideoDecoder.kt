package com.convx.music.ui.player.video

import android.content.Context
import android.os.Looper
import androidx.annotation.MainThread
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MediaSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Allows a single foreground visual player to prepare its media at a time. */
internal object PlayerVideoDecoder {
    enum class Use { LOOP, BACKGROUND, OFFICIAL }

    const val PRIORITY_BACKGROUND = 0
    const val PRIORITY_LOOP = 1
    const val PRIORITY_OFFICIAL = 2

    private data class Owner(
        val player: ExoPlayer,
        val priority: Int,
        val onRevoked: () -> Unit,
    )

    private var owner: Owner? = null
    private val vacancy = MutableStateFlow(true)
    val available: StateFlow<Boolean> = vacancy.asStateFlow()

    @MainThread
    fun build(context: Context, mediaSourceFactory: MediaSource.Factory, use: Use): ExoPlayer {
        requireMainThread()
        return ExoPlayer.Builder(context)
            .setMediaSourceFactory(mediaSourceFactory)
            .setLoadControl(
                DefaultLoadControl.Builder()
                    .setBufferDurationsMs(
                        PlayerVideoBuffers.MIN_BUFFER_MS,
                        PlayerVideoBuffers.MAX_BUFFER_MS,
                        PlayerVideoBuffers.START_BUFFER_MS,
                        PlayerVideoBuffers.RESUME_BUFFER_MS,
                    )
                    .build(),
            )
            .build()
            .apply {
                trackSelectionParameters = trackSelectionParameters.buildUpon()
                    .setTrackTypeDisabled(C.TRACK_TYPE_AUDIO, true)
                    .apply {
                        when (use) {
                            Use.LOOP -> setMaxVideoFrameRate(30).setMaxVideoBitrate(2_500_000)
                            Use.BACKGROUND -> setMaxVideoSize(1_280, 1_280)
                            Use.OFFICIAL -> Unit
                        }
                    }
                    .build()
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(C.USAGE_MEDIA)
                        .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
                        .build(),
                    false,
                )
                volume = 0f
                if (use != Use.OFFICIAL) repeatMode = Player.REPEAT_MODE_ONE
            }
    }

    @MainThread
    fun claim(player: ExoPlayer, priority: Int, onRevoked: () -> Unit = {}): Boolean {
        requireMainThread()
        val previous = owner
        if (previous?.player === player) return true
        if (previous != null && previous.priority > priority) return false

        owner = Owner(player, priority, onRevoked)
        vacancy.value = false
        previous?.stop()
        return true
    }

    @MainThread
    fun owns(player: ExoPlayer): Boolean {
        requireMainThread()
        return owner?.player === player
    }

    @MainThread
    fun release(player: ExoPlayer) {
        requireMainThread()
        val previous = owner ?: return
        if (previous.player !== player) return

        owner = null
        previous.stop()
        vacancy.value = owner == null
    }

    private fun Owner.stop() {
        player.pause()
        player.stop()
        onRevoked()
    }

    private fun requireMainThread() {
        check(Looper.myLooper() == Looper.getMainLooper()) { "Video decoder ownership is main-thread only" }
    }
}
