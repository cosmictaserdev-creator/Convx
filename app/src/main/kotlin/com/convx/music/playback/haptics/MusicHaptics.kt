/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
package com.convx.music.playback.haptics

import android.content.Context
import android.media.AudioAttributes
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.Process
import android.os.SystemClock
import android.os.VibrationAttributes
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.net.toUri
import androidx.media3.common.Player
import com.convx.music.constants.MusicHapticsLeadKey
import com.convx.music.utils.dataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.math.abs

data class HapticSettings(
    val enabled: Boolean = false,
    /** 0 to 1, tuned behaviour at 0.5. */
    val sensitivity: Float = 0.5f,
    /** 0 to 1, tuned behaviour at 0.5. */
    val strength: Float = 0.5f,
)

/**
 * Vibrates to the beat of songs stored on this device.
 *
 * Each song is decoded once and its hits are found ahead of time. The app's main thread only
 * reads the player's position a few times a second; a separate high-priority thread then fires
 * every pulse against that position using the system clock, so a busy screen or a slow
 * vibration call cannot make the beat stutter. A seek, pause or speed change is noticed at the
 * next position reading and the pulses are lined up again.
 *
 * It watches the player on its own, so it only needs to be created once while the service starts.
 */
class MusicHaptics(
    private val context: Context,
    private val scope: CoroutineScope,
    private val player: () -> Player,
    settings: Flow<HapticSettings>,
) {
    // Main thread only.
    private var current = HapticSettings()
    private var currentId: String? = null
    private var analyzed: HapticTrack? = null
    private var analysisJob: Job? = null
    private val cache = object : LinkedHashMap<String, HapticTrack>(16, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, HapticTrack>?): Boolean =
            size > CACHE_SIZE
    }

    @Volatile private var gain = 1f

    /** How much earlier than the beat to fire, on top of [LEAD_MS]; set by the user. */
    @Volatile private var userLeadMs = DEFAULT_LEAD_MS

    // Timing thread only.
    private val thread = HandlerThread("MusicHaptics", Process.THREAD_PRIORITY_URGENT_AUDIO).also { it.start() }
    private val handler = Handler(thread.looper)
    private var timerId: String? = null
    private var track: HapticTrack? = null
    private var anchored = false
    private var anchorPos = 0L
    private var anchorUptime = 0L
    private var anchorSpeed = 1f
    private var nextIndex = 0
    private val fireRunnable = Runnable { fire() }

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= 31) {
        context.getSystemService(VibratorManager::class.java)?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    private val settingsJob: Job = scope.launch {
        settings.distinctUntilChanged().collect { next ->
            current = next
            gain = 0.4f + 1.2f * next.strength.coerceIn(0f, 1f)
            if (!next.enabled) {
                stop()
            } else {
                val id = currentId
                val base = analyzed
                if (id != null && base != null) {
                    val selected = base.select(next.sensitivity)
                    handler.post { setTrack(id, selected) }
                }
                refresh()
            }
        }
    }

    private val leadJob: Job = scope.launch {
        context.dataStore.data
            .map { it[MusicHapticsLeadKey] ?: DEFAULT_LEAD_MS.toFloat() }
            .distinctUntilChanged()
            .collect { value ->
                userLeadMs = value.toLong().coerceIn(0L, 300L)
                handler.post { reschedule() }
            }
    }

    private val watchJob: Job = scope.launch {
        while (true) {
            delay(WATCH_INTERVAL_MS)
            refresh()
        }
    }

    /** Main thread: reads what is playing and hands the position to the timing thread. */
    private fun refresh() {
        if (!current.enabled) return
        val active = player()
        val id = active.currentMediaItem?.mediaId
        if (id == null || !(id.startsWith("content://") || id.startsWith("file://"))) {
            stop()
            return
        }
        if (id != currentId) {
            currentId = id
            analyzed = null
            analysisJob?.cancel()
            handler.post { setTrack(id, null) }
            val cached = cache[id]
            if (cached != null) {
                analyzed = cached
                val selected = cached.select(current.sensitivity)
                handler.post { setTrack(id, selected) }
            } else {
                analysisJob = scope.launch {
                    val result = analyzeHaptics(context, id.toUri())
                    if (currentId != id) return@launch
                    if (result != null) cache[id] = result
                    analyzed = result
                    val selected = result?.select(current.sensitivity)
                    handler.post { setTrack(id, selected) }
                }
            }
        }
        val position = active.currentPosition
        val uptime = SystemClock.uptimeMillis()
        val speed = active.playbackParameters.speed.takeIf { it > 0f } ?: 1f
        val playing = active.isPlaying
        handler.post { onSnapshot(id, position, uptime, speed, playing) }
    }

    private fun stop() {
        if (currentId == null) return
        analysisJob?.cancel()
        currentId = null
        analyzed = null
        handler.post { clear() }
    }

    // ---- Timing thread ----

    private fun setTrack(id: String, newTrack: HapticTrack?) {
        if (id != timerId) {
            timerId = id
            anchored = false
        }
        track = newTrack
        reschedule()
    }

    private fun clear() {
        handler.removeCallbacks(fireRunnable)
        timerId = null
        track = null
        anchored = false
    }

    private fun onSnapshot(id: String, positionMs: Long, uptimeMs: Long, speed: Float, playing: Boolean) {
        if (id != timerId) return
        if (!playing) {
            anchored = false
            handler.removeCallbacks(fireRunnable)
            return
        }
        val drift = if (anchored) abs(predict(uptimeMs) - positionMs) else Long.MAX_VALUE
        if (!anchored || drift > RESYNC_MS || speed != anchorSpeed) {
            anchorPos = positionMs
            anchorUptime = uptimeMs
            anchorSpeed = speed
            anchored = true
            reschedule()
        }
    }

    /** Where the song is at [uptimeMs], extrapolated from the last anchor. */
    private fun predict(uptimeMs: Long): Long = anchorPos + ((uptimeMs - anchorUptime) * anchorSpeed).toLong()

    /** The clock time at which pulse [index] should start, including the lead. */
    private fun fireTime(pulses: HapticTrack, index: Int): Long {
        val lead = LEAD_MS + userLeadMs
        return anchorUptime + ((pulses.times[index] - lead - anchorPos) / anchorSpeed).toLong()
    }

    private fun reschedule() {
        handler.removeCallbacks(fireRunnable)
        val pulses = track ?: return
        if (!anchored) return
        val lead = LEAD_MS + userLeadMs
        val now = SystemClock.uptimeMillis()
        val index = pulses.indexAt(predict(now) + lead - (STALE_MS * anchorSpeed).toLong())
        nextIndex = index
        if (index < pulses.size) handler.postAtTime(fireRunnable, fireTime(pulses, index))
    }

    private fun fire() {
        val pulses = track ?: return
        if (!anchored) return
        val index = nextIndex
        if (index >= pulses.size) return
        // A pulse that is already too late would land off the beat, so it is dropped.
        if (SystemClock.uptimeMillis() - fireTime(pulses, index) <= STALE_MS) {
            pulse((pulses.intensity[index] / 255f * gain).coerceIn(0f, 1f), pulses.sharpness[index] / 255f)
        }
        val now = SystemClock.uptimeMillis()
        var next = index + 1
        while (next < pulses.size && fireTime(pulses, next) < now - STALE_MS) next++
        nextIndex = next
        if (next < pulses.size) handler.postAtTime(fireRunnable, fireTime(pulses, next))
    }

    private fun pulse(intensity: Float, sharpness: Float) {
        val motor = vibrator ?: return
        if (!motor.hasVibrator() || intensity <= 0f) return
        runCatching {
            // Without tuned primitives the motor is often an eccentric mass that needs tens of
            // milliseconds to spin up and stays still at low amplitudes, so shorter or weaker
            // one-shots would not be felt at all.
            val effect = composed(motor, intensity, sharpness)
                ?: if (motor.hasAmplitudeControl()) {
                    // Bass hits are longer and softer; bright hits are short and crisp.
                    VibrationEffect.createOneShot(
                        (20 + 20 * (1 - sharpness)).toLong(),
                        (MIN_AMPLITUDE + (255 - MIN_AMPLITUDE) * intensity).toInt().coerceIn(MIN_AMPLITUDE, 255),
                    )
                } else if (intensity >= 0.35f) {
                    // On/off motors cannot vary strength; stronger accents last longer.
                    VibrationEffect.createOneShot((25 + 25 * intensity).toLong(), VibrationEffect.DEFAULT_AMPLITUDE)
                } else {
                    return
                }
            // Media usage honours the system's separate media vibration switch.
            if (Build.VERSION.SDK_INT >= 33) {
                motor.vibrate(effect, VibrationAttributes.createForUsage(VibrationAttributes.USAGE_MEDIA))
            } else {
                @Suppress("DEPRECATION")
                motor.vibrate(
                    effect,
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build(),
                )
            }
        }
    }

    /** Device-tuned primitives feel cleaner than raw motor amplitudes. */
    private fun composed(motor: Vibrator, intensity: Float, sharpness: Float): VibrationEffect? {
        if (Build.VERSION.SDK_INT < 31) return null
        val primitive = if (sharpness < 0.5f) {
            VibrationEffect.Composition.PRIMITIVE_THUD
        } else {
            VibrationEffect.Composition.PRIMITIVE_CLICK
        }
        if (!motor.areAllPrimitivesSupported(primitive)) return null
        return VibrationEffect.startComposition().addPrimitive(primitive, intensity).compose()
    }

    fun release() {
        settingsJob.cancel()
        leadJob.cancel()
        watchJob.cancel()
        analysisJob?.cancel()
        currentId = null
        analyzed = null
        handler.post { clear() }
        thread.quitSafely()
        cache.clear()
    }

    private companion object {
        /** Motors need a moment to spin up, so pulses always fire slightly early. */
        const val LEAD_MS = 12L

        /** The user's extra lead before they change it, in milliseconds. */
        const val DEFAULT_LEAD_MS = 30L

        /** A pulse more than this late is dropped instead of firing off the beat. */
        const val STALE_MS = 40L

        /** The position must drift this far from the prediction before pulses are re-aligned. */
        const val RESYNC_MS = 35L
        const val WATCH_INTERVAL_MS = 200L
        const val CACHE_SIZE = 8

        /** The lowest amplitude that reliably moves an eccentric-mass motor. */
        const val MIN_AMPLITUDE = 70
    }
}
