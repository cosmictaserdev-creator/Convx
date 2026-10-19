/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.convx.music.playback.MusicService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
open class MusicWidgetReceiver : AppWidgetProvider() {
    @Inject lateinit var widgetManager: ConvxWidgetManager

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // Only trigger update through MusicService if it's already running
        // This prevents BackgroundServiceStartNotAllowedException on Android 14+
        if (MusicService.isRunning) {
            val intent = Intent(context, MusicService::class.java).apply {
                action = ACTION_UPDATE_WIDGET
            }
            try {
                context.startService(intent)
            } catch (e: Exception) {
                // Service might be restricted in background
            }
        }
        if (!MusicService.isRunning) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    widgetManager.updateWidgets(
                        title = context.getString(com.convx.music.R.string.app_name),
                        artist = context.getString(com.convx.music.R.string.widget_open_app),
                        artworkUri = null, isPlaying = false, isLiked = false,
                    )
                } catch (error: Exception) {
                    timber.log.Timber.w(error, "Could not initialize widgets")
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        if (!MusicService.isRunning) {
            onUpdate(context, appWidgetManager, intArrayOf(appWidgetId))
            return
        }
        // Trigger widget update when size changes
        if (MusicService.isRunning) {
            val intent = Intent(context, MusicService::class.java).apply {
                action = ACTION_UPDATE_WIDGET
            }
            try {
                context.startService(intent)
            } catch (e: Exception) {
                // Service might be restricted in background
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        when (intent.action) {
            ACTION_PLAY_PAUSE, ACTION_LIKE, ACTION_NEXT, ACTION_PREVIOUS -> {
                // User interactions from widget buttons can start the service.
                // Use startForegroundService so the system grants FGS privilege
                // on Android 12+ (API 31+) where background FGS starts are banned.
                val serviceIntent = Intent(context, MusicService::class.java).apply {
                    action = intent.action
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
        const val ACTION_PLAY_PAUSE = "com.convx.music.widget.PLAY_PAUSE"
        const val ACTION_LIKE = "com.convx.music.widget.LIKE"
        const val ACTION_NEXT = "com.convx.music.widget.NEXT"
        const val ACTION_PREVIOUS = "com.convx.music.widget.PREVIOUS"
        const val ACTION_UPDATE_WIDGET = "com.convx.music.widget.UPDATE_WIDGET"
    }
}
