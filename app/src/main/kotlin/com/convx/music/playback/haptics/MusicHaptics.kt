/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
package com.convx.music.playback.haptics

import android.content.Context
import android.media.AudioAttributes
import android.os.Build
import android.os.VibrationAttributes
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.net.toUri
import androidx.media3.common.Player
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlin.math.max

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
 * Each song is decoded once, its hits are found ahead of time, and playback only schedules the
 * next pulse against the player's live position. Every pulse re-reads that position, so seeks,
 * pauses and speed changes correct the timing within one beat.
 *
 * Call [refresh] from the player's event callback; everything runs on [scope]'s (main) thread.
 */
class MusicHaptics(
    private val context: Context,
    private val scope: CoroutineScope,
    private val player: () -> Player,
    settings: Flow<HapticSettings>,
) {
    private var current = HapticSettings()
    private var gain = 1f

    private var currentId: String? = null
    private var analyzed: HapticTrack? = null
    private var track: HapticTrack? = null
    private var analysisJob: Job? = null
    private var timerJob: Job? = null
    private val cache = object : LinkedHashMap<String, HapticTrack>(16, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, HapticTrack>?): Boolean =
            size > CACHE_SIZE
    }

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
                analyzed?.let { track = it.select(next.sensitivity) }
                refresh()
            }
        }
    }

    /** Re-reads what is playing and reschedules the next pulse. */
    fun refresh() {
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
            track = null
            timerJob?.cancel()
            analysisJob?.cancel()
            val cached = cache[id]
            if (cached != null) {
                analyzed = cached
                track = cached.select(current.sensitivity)
            } else {
                analysisJob = scope.launch {
                    val result = analyzeHaptics(context, id.toUri())
                    if (currentId != id) return@launch
                    if (result != null) cache[id] = result
                    analyzed = result
                    track = result?.select(current.sensitivity)
                    schedule()
                }
                return
            }
        }
        schedule()
    }

    private fun stop() {
        timerJob?.cancel()
        analysisJob?.cancel()
        currentId = null
        analyzed = null
        track = null
    }

    private fun schedule(from: Int = 0) {
        timerJob?.cancel()
        val pulses = track ?: return
        val active = player()
        if (!active.isPlaying) return
        val position = active.currentPosition
        val index = max(from, pulses.indexAt(position + LEAD_MS))
        if (index >= pulses.size) return
        val speed = active.playbackParameters.speed.takeIf { it > 0f } ?: 1f
        val wait = ((pulses.times[index] - position) / speed).toLong()
        val id = currentId
        timerJob = scope.launch {
            delay(max(0L, wait - LEAD_MS))
            fire(index, id)
        }
    }

    private fun fire(index: Int, id: String?) {
        if (id != currentId) return
        val pulses = track ?: return
        if (index >= pulses.size) return
        val active = player()
        if (!active.isPlaying || active.currentMediaItem?.mediaId != id) return
        // A late timer or a position correction makes the pulse stale.
        val offset = pulses.times[index] - active.currentPosition - LEAD_MS
        if (offset > 40) {
            schedule()
            return
        }
        if (offset >= -40) {
            pulse((pulses.intensity[index] / 255f * gain).coerceIn(0f, 1f), pulses.sharpness[index] / 255f)
        }
        schedule(index + 1)
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
/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
package com.convx.music.playback.haptics

import android.content.Context
import android.media.AudioAttributes
import android.os.Build
import android.os.SystemClock
import android.os.VibrationAttributes
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.net.toUri
import androidx.media3.common.Player
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max

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
 * Each song is decoded once, its hits are found ahead of time, and playback only schedules the
 * next pulse against the player's live position. Every pulse re-reads that position, so seeks,
 * pauses and speed changes correct the timing within one beat.
 *
 * It watches the player on its own (a light check a few times a second), so it only needs to be
 * created once while the service starts. Everything runs on [scope]'s (main) thread.
 */
class MusicHaptics(
    private val context: Context,
    private val scope: CoroutineScope,
    private val player: () -> Player,
    settings: Flow<HapticSettings>,
) {
    private var current = HapticSettings()
    private var gain = 1f

    private var currentId: String? = null
    private var analyzed: HapticTrack? = null
    private var track: HapticTrack? = null
    private var analysisJob: Job? = null
    private var timerJob: Job? = null
    private var pendingIndex = -1
    private var dueAt = 0L
    private val cache = object : LinkedHashMap<String, HapticTrack>(16, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, HapticTrack>?): Boolean =
            size > CACHE_SIZE
    }

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
                analyzed?.let { track = it.select(next.sensitivity) }
                refresh()
            }
        }
    }

    private val watchJob: Job = scope.launch {
        while (true) {
            delay(WATCH_INTERVAL_MS)
            tick()
        }
    }

    /** Leaves a pending pulse alone while it still matches the player's position. */
    private fun tick() {
        if (!current.enabled) return
        val pulses = track
        val active = player()
        if (timerJob?.isActive == true && pulses != null && active.isPlaying &&
            pendingIndex in 0 until pulses.size && active.currentMediaItem?.mediaId == currentId
        ) {
            val speed = active.playbackParameters.speed.takeIf { it > 0f } ?: 1f
            val expected = ((pulses.times[pendingIndex] - active.currentPosition) / speed).toLong() - LEAD_MS
            val actual = dueAt - SystemClock.uptimeMillis()
            if (abs(expected - actual) <= 60) return
        }
        refresh()
    }

    /** Re-reads what is playing and reschedules the next pulse. */
    fun refresh() {
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
            track = null
            timerJob?.cancel()
            analysisJob?.cancel()
            val cached = cache[id]
            if (cached != null) {
                analyzed = cached
                track = cached.select(current.sensitivity)
            } else {
                analysisJob = scope.launch {
                    val result = analyzeHaptics(context, id.toUri())
                    if (currentId != id) return@launch
                    if (result != null) cache[id] = result
                    analyzed = result
                    track = result?.select(current.sensitivity)
                    schedule()
                }
                return
            }
        }
        schedule()
    }

    private fun stop() {
        timerJob?.cancel()
        analysisJob?.cancel()
        currentId = null
        analyzed = null
        track = null
    }

    private fun schedule(from: Int = 0) {
        timerJob?.cancel()
        val pulses = track ?: return
        val active = player()
        if (!active.isPlaying) return
        val position = active.currentPosition
        val index = max(from, pulses.indexAt(position + LEAD_MS))
        if (index >= pulses.size) return
        val speed = active.playbackParameters.speed.takeIf { it > 0f } ?: 1f
        val wait = ((pulses.times[index] - position) / speed).toLong()
        val id = currentId
        val pause = max(0L, wait - LEAD_MS)
        pendingIndex = index
        dueAt = SystemClock.uptimeMillis() + pause
        timerJob = scope.launch {
            delay(pause)
            fire(index, id)
        }
    }

    private fun fire(index: Int, id: String?) {
        if (id != currentId) return
        val pulses = track ?: return
        if (index >= pulses.size) return
        val active = player()
        if (!active.isPlaying || active.currentMediaItem?.mediaId != id) return
        // A late timer or a position correction makes the pulse stale.
        val offset = pulses.times[index] - active.currentPosition - LEAD_MS
        if (offset > 40) {
            schedule()
            return
        }
        if (offset >= -40) {
            pulse((pulses.intensity[index] / 255f * gain).coerceIn(0f, 1f), pulses.sharpness[index] / 255f)
        }
        schedule(index + 1)
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
        watchJob.cancel()
        stop()
        cache.clear()
    }

    private companion object {
        /** Motors need a moment to spin up, so pulses fire slightly early. */
        const val LEAD_MS = 12L
        const val CACHE_SIZE = 8
        const val WATCH_INTERVAL_MS = 250L

        /** The lowest amplitude that reliably moves an eccentric-mass motor. */
        const val MIN_AMPLITUDE = 70
    }
}
*/
        const val LEAD_MS = 12L
        const val CACHE_SIZE = 8

        /** The lowest amplitude that reliably moves an eccentric-mass motor. */
        const val MIN_AMPLITUDE = 70
    }
}
