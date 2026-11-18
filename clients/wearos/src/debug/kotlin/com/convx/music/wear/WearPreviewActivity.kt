package com.convx.music.wear

import android.os.Bundle
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.convx.music.playback.watch.WearLyricLine
import com.convx.music.playback.watch.WearSnapshot
import com.convx.music.playback.watch.WearWord

class WearPreviewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.post { window.insetsController?.hide(android.view.WindowInsets.Type.systemBars()) }
        val link = (application as WearApplication).graph.koin.get<WearPhoneLink>()
        val sample = WearLinkState(connected = true, connecting = false, receivedAtMs = SystemClock.elapsedRealtime(),
            snapshot = WearSnapshot(sessionId = "preview", sequence = 1, mediaId = "preview-track",
                title = "A Moment in Motion", artist = "Convx preview", isPlaying = true, positionMs = 3_200,
                durationMs = 180_000, canSkipNext = true, canSkipPrevious = true, volume = .7f,
                lyrics = listOf(WearLyricLine(0, "A quiet night"),
                    WearLyricLine(3_000, "Let the music play", listOf(WearWord(3_000, 3_500, "Let"),
                        WearWord(3_500, 4_000, "the"), WearWord(4_000, 4_600, "music"), WearWord(4_600, 5_000, "play"))),
                    WearLyricLine(10_000, "One song at a time"))))
        setContent { WearScreen(sample, link, initialLyrics = intent.getBooleanExtra("lyrics", false)) }
    }
}
