/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.releases

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.convx.music.MainActivity
import com.convx.music.R
import com.convx.music.playback.Announcement
import com.convx.music.playback.ReleaseAnnouncer

class ReleaseRadarNotifier(private val context: Context) : ReleaseAnnouncer {
    /** Posts [announcement] unless notifications are off; returns whether it was posted. */
    @SuppressLint("MissingPermission")
    override fun announce(announcement: Announcement): Boolean {
        val manager = NotificationManagerCompat.from(context)
        if (!manager.areNotificationsEnabled()) return false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                NotificationChannel(CHANNEL_ID, context.getString(R.string.new_releases_channel), NotificationManager.IMPORTANCE_LOW).apply {
                    description = context.getString(R.string.new_releases_channel_desc)
                },
            )
        }
        val open = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        manager.notify(
            NOTIFICATION_ID,
            NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.convx_notification)
                .setContentTitle(announcement.title)
                .setContentText(announcement.text)
                .setContentIntent(open)
                .setAutoCancel(true)
                .build(),
        )
        return true
    }

    private companion object {
        const val CHANNEL_ID = "new_releases"
        const val NOTIFICATION_ID = 4101
    }
}
