package com.convx.music.network

import android.app.Activity
import android.app.Instrumentation
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.os.Bundle
import android.os.IBinder
import android.view.Choreographer
import androidx.datastore.preferences.core.edit
import com.convx.music.MainActivity
import com.convx.music.constants.*
import com.convx.music.db.entities.Song
import com.convx.music.db.entities.SongEntity
import com.convx.music.extensions.toMediaItem
import com.convx.music.playback.MusicService
import com.convx.music.utils.dataStore
import com.convx.music.ui.player.PlayerMorph
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import java.io.File
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/** Exercises the actual expanded player while retargeting designs with artwork shadows enabled. */
class PlayerDesignStabilityInstrumentation : Instrumentation() {
    override fun onCreate(arguments: Bundle?) { super.onCreate(arguments); start() }

    override fun onStart() {
        val report = StringBuilder()
        val store = targetContext.dataStore
        val previous = runBlocking { store.data.first() }
        var music: MusicService? = null
        val connected = CountDownLatch(1)
        val connection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
                music = (binder as MusicService.MusicBinder).service
                connected.countDown()
            }
            override fun onServiceDisconnected(name: ComponentName?) { music = null }
        }
        var bound = false
        var activity: Activity? = null
        var oldItems = emptyList<androidx.media3.common.MediaItem>()
        var oldIndex = 0
        var oldPosition = 0L
        var succeeded = false
        val fixture = File(targetContext.cacheDir, "shadow-stress-art.png")
        val audioFixture = File(targetContext.cacheDir, "shadow-stress.wav")
        try {
            val wav = java.nio.ByteBuffer.allocate(44 + 16_000).order(java.nio.ByteOrder.LITTLE_ENDIAN)
            wav.put("RIFF".toByteArray()).putInt(36 + 16_000).put("WAVEfmt ".toByteArray())
                .putInt(16).putShort(1).putShort(1).putInt(8000).putInt(16_000).putShort(2).putShort(16)
                .put("data".toByteArray()).putInt(16_000)
            audioFixture.writeBytes(wav.array())
            Bitmap.createBitmap(64, 64, Bitmap.Config.ARGB_8888).apply {
                eraseColor(android.graphics.Color.rgb(65, 94, 155))
                fixture.outputStream().use { compress(Bitmap.CompressFormat.PNG, 100, it) }
                recycle()
            }
            activity = startActivitySync(Intent(targetContext, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            bound = targetContext.bindService(Intent(targetContext, MusicService::class.java), connection, Context.BIND_AUTO_CREATE)
            check(bound && connected.await(20, TimeUnit.SECONDS)) { "Playback service did not connect" }
            val service = requireNotNull(music)
            runBlocking { withTimeout(20_000) { service.isPlayerReady.first { it } } }
            runOnMainSync {
                val player = service.player
                oldItems = (0 until player.mediaItemCount).map(player::getMediaItemAt)
                oldIndex = player.currentMediaItemIndex.coerceAtLeast(0)
                oldPosition = player.currentPosition
                player.pause()
                player.setMediaItem(Song(SongEntity(
                    id = android.net.Uri.fromFile(audioFixture).toString(),
                    title = "Shadow stress track", thumbnailUrl = android.net.Uri.fromFile(fixture).toString(), duration = 1, isLocal = true,
                ), emptyList()).toMediaItem())
                player.prepare()
            }
            var miniReady = false
            repeat(100) {
                if (!miniReady) {
                    runOnMainSync { miniReady = PlayerMorph.miniLayer != null }
                    if (!miniReady) Thread.sleep(100)
                }
            }
            check(miniReady) { "Mini player did not render" }
            runOnMainSync {
                PlayerMorph.onDragStart(true)
                PlayerMorph.onDragDelta(2000f)
                PlayerMorph.onDragEnd(2000f)
            }
            Thread.sleep(1200)
            var expanded = false
            runOnMainSync { expanded = PlayerMorph.progress >= 0.95f }
            check(expanded) { "Player did not expand" }
            val cases = listOf(
                Triple(false, PlayerArtworkStyle.CARD, PlayerLook.SQUARED),
                Triple(false, PlayerArtworkStyle.VINYL, PlayerLook.SQUARED),
                Triple(false, PlayerArtworkStyle.CLOVER, PlayerLook.SQUARED),
                Triple(true, PlayerArtworkStyle.CARD, PlayerLook.SQUARED),
                Triple(true, PlayerArtworkStyle.CARD, PlayerLook.HERO),
            )
            repeat(4) { pass ->
                for ((v2, art, look) in cases) {
                    runBlocking { store.edit {
                        it[UseAppleMusicPlayerKey] = v2
                        it[PlayerArtworkStyleKey] = art.name
                        it[PlayerLookKey] = look.name
                        it[ShowPlayerThumbnailShadowKey] = true
                        it[PlayerThumbnailShadowElevationKey] = if (pass % 2 == 0) 8f else 24f
                    } }
                    Thread.sleep(700)
                    val frame = CountDownLatch(1)
                    runOnMainSync { Choreographer.getInstance().postFrameCallback { frame.countDown() } }
                    check(frame.await(5, TimeUnit.SECONDS)) { "UI stopped producing frames" }
                    check(activity?.isFinishing == false)
                    val screenshot = requireNotNull(uiAutomation.takeScreenshot()) { "No rendered frame" }
                    screenshot.recycle()
                    val message = "pass=$pass v2=$v2 artwork=$art look=$look shadow=ON: frame OK"
                    report.appendLine(message)
                    sendStatus(1, Bundle().apply { putString("stream", message + "\n") })
                }
            }
            succeeded = true
        } catch (error: Exception) {
            report.appendLine("FAILED: ${error.javaClass.simpleName}: ${error.message}")
        } finally {
            runBlocking { store.edit { current ->
                listOf(UseAppleMusicPlayerKey, ShowPlayerThumbnailShadowKey).forEach { key ->
                    previous[key]?.let { current[key] = it } ?: current.remove(key)
                }
                listOf(PlayerArtworkStyleKey, PlayerLookKey).forEach { key ->
                    previous[key]?.let { current[key] = it } ?: current.remove(key)
                }
                previous[PlayerThumbnailShadowElevationKey]?.let { current[PlayerThumbnailShadowElevationKey] = it }
                    ?: current.remove(PlayerThumbnailShadowElevationKey)
            } }
            music?.let { service -> runOnMainSync {
                if (oldItems.isEmpty()) service.player.clearMediaItems()
                else service.player.setMediaItems(oldItems, oldIndex.coerceAtMost(oldItems.lastIndex), oldPosition)
            } }
            if (bound) targetContext.unbindService(connection)
            fixture.delete()
            audioFixture.delete()
        }
        finish(if (succeeded) Activity.RESULT_OK else Activity.RESULT_CANCELED,
            Bundle().apply { putString("stream", report.toString()) })
    }
}
