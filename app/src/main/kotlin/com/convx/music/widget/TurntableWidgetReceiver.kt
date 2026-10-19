/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.convx.music.playback.MusicService

class TurntableWidgetReceiver : MusicWidgetReceiver() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        when (intent.action) {
            ACTION_TURNTABLE_PLAY_PAUSE, ACTION_TURNTABLE_NEXT, ACTION_TURNTABLE_PREVIOUS -> {
                // User interactions from widget buttons can start the service
                // Android allows starting FGS from widget PendingIntent clicks
                val serviceIntent = Intent(context, MusicService::class.java).apply {
                    action = when (intent.action) {
                        ACTION_TURNTABLE_PLAY_PAUSE -> MusicWidgetReceiver.ACTION_PLAY_PAUSE
                        ACTION_TURNTABLE_NEXT -> MusicWidgetReceiver.ACTION_NEXT
                        ACTION_TURNTABLE_PREVIOUS -> MusicWidgetReceiver.ACTION_PREVIOUS
                        else -> intent.action
                    }
                    putExtras(intent)
                }
                try {
                    ContextCompat.startForegroundService(context, serviceIntent)
                } catch (e: Exception) {
                    // Service might be restricted in background
                }
            }
        }
    }

    companion object {
        const val ACTION_TURNTABLE_PLAY_PAUSE = "com.convx.music.widget.TURNTABLE_PLAY_PAUSE"
        const val ACTION_TURNTABLE_NEXT = "com.convx.music.widget.TURNTABLE_NEXT"
        const val ACTION_TURNTABLE_PREVIOUS = "com.convx.music.widget.TURNTABLE_PREVIOUS"
        const val ACTION_UPDATE_TURNTABLE_WIDGET = "com.convx.music.widget.UPDATE_TURNTABLE_WIDGET"
    }
}
