/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.widget

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.convxy.music.playback.MusicService

/**
 * Apple Music styled "Now Playing" widget (row 4x1 / square 2x2 / wide 4x2).
 *
 * Behaviour is identical to [MusicWidgetReceiver] — poke MusicService on update,
 * forward transport clicks — and the manager's transport PendingIntents already
 * target MusicWidgetReceiver explicitly, so they work from this widget as-is.
 * Only the rendering differs, so this exists purely to be a separate
 * AppWidgetProvider component with its own layouts and per-instance config.
 */
class NowPlayingWidgetReceiver : MusicWidgetReceiver() {

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        // Otherwise every removed widget leaves its settings behind forever.
        WidgetConfig.delete(context, appWidgetIds)
    }

    companion object {
        /**
         * Repaint now rather than waiting for the next playback event — used after
         * the config screen changes something. No-op when the service isn't running,
         * since a background start would throw on Android 14+.
         */
        fun requestUpdate(context: Context) {
            if (!MusicService.isRunning) return
            runCatching {
                context.startService(
                    Intent(context, MusicService::class.java).apply {
                        action = ACTION_UPDATE_WIDGET
                    }
                )
            }
        }
    }
}
