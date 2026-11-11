/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.playback

import android.media.AudioAttributes
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.media.AudioMixerAttributes
import android.os.Build
import androidx.annotation.RequiresApi
import timber.log.Timber

/**
 * Asks Android to give music playback a bit-perfect path to a connected USB DAC (Android 14+).
 *
 * This is the platform's own version of "USB exclusive mode": the system mixer stops resampling or
 * mixing music and hands the samples to the DAC as they are, bypassing the volume and effects chain.
 * Apps that implement their own USB driver reach the same goal by detaching the kernel driver, which
 * is a much heavier route; the mixer attributes API needs no extra permission beyond the
 * MODIFY_AUDIO_SETTINGS the app already holds.
 */
object UsbBitPerfectAudio {
    private const val TAG = "UsbBitPerfect"

    private val musicAttributes: AudioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        .build()

    val isSupported: Boolean get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE

    private fun isUsbOutput(device: AudioDeviceInfo): Boolean =
        device.type == AudioDeviceInfo.TYPE_USB_DEVICE || device.type == AudioDeviceInfo.TYPE_USB_HEADSET

    /**
     * Turns the bit-perfect mixer on or off for every connected USB output. Safe to call repeatedly,
     * for example when the setting changes or a DAC is plugged in. A device that offers no bit-perfect
     * mode is left on the normal mixer.
     */
    fun apply(audioManager: AudioManager, enabled: Boolean) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) return
        applyApi34(audioManager, enabled)
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private fun applyApi34(audioManager: AudioManager, enabled: Boolean) {
        runCatching {
            audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
                .filter(::isUsbOutput)
                .forEach { device -> configure(audioManager, device, enabled) }
        }.onFailure { Timber.tag(TAG).w(it, "Could not update the USB mixer mode") }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private fun configure(audioManager: AudioManager, device: AudioDeviceInfo, enabled: Boolean) {
        if (!enabled) {
            val cleared = audioManager.clearPreferredMixerAttributes(musicAttributes, device)
            Timber.tag(TAG).d("Cleared bit-perfect for ${device.productName}: $cleared")
            return
        }
        // The highest-rate, widest-encoding bit-perfect format the DAC advertises.
        val best = audioManager.getSupportedMixerAttributes(device)
            .filter { it.mixerBehavior == AudioMixerAttributes.MIXER_BEHAVIOR_BIT_PERFECT }
            .maxWithOrNull(
                compareBy<AudioMixerAttributes> { it.format.sampleRate }
                    .thenBy { it.format.encoding },
            )
        if (best == null) {
            Timber.tag(TAG).i("${device.productName} offers no bit-perfect mode")
            return
        }
        val set = audioManager.setPreferredMixerAttributes(musicAttributes, device, best)
        Timber.tag(TAG).i("Bit-perfect on ${device.productName} at ${best.format.sampleRate} Hz: $set")
    }
}
