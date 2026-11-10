package com.convx.music.eq


import android.annotation.SuppressLint
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import com.convx.music.eq.audio.CustomEqualizerAudioProcessor
import com.convx.music.eq.data.ParametricEQ
import com.convx.music.eq.data.SavedEQProfile
import timber.log.Timber

/**
 * Service for managing custom EQ using ExoPlayer's AudioProcessor
 * Supports 10+ band Parametric EQ format (APO)
 */
class EqualizerService {

    @SuppressLint("UnsafeOptInUsageError")
    private val audioProcessors = mutableListOf<CustomEqualizerAudioProcessor>()
    private var pendingProfile: SavedEQProfile? = null
    private var shouldDisable: Boolean = false
    private var bitPerfectBypass: Boolean = false

    companion object {
        private const val TAG = "EqualizerService"
    }

    /**
     * Add an audio processor instance
     * This should be called when ExoPlayer is initialized
     */
    @OptIn(UnstableApi::class)
    fun addAudioProcessor(processor: CustomEqualizerAudioProcessor) {
        audioProcessors.add(processor)
        processor.forceBypass = bitPerfectBypass
        Timber.tag(TAG).d("Audio processor added. Total: ${audioProcessors.size}")

        // Apply pending profile if one was set before processor was available
        if (shouldDisable) {
            processor.disable()
            // Don't clear shouldDisable here, as we might add more processors
        } else {
            pendingProfile?.let { profile ->
                applyProfileToProcessor(processor, profile)
            }
            // Don't clear pendingProfile here
        }
    }

    /**
     * Remove an audio processor instance
     */
    fun removeAudioProcessor(processor: CustomEqualizerAudioProcessor) {
        audioProcessors.remove(processor)
    }

    /**
     * Apply an EQ profile
     * If audio processor is not set, stores as pending profile
     */
    @OptIn(UnstableApi::class)
    fun applyProfile(profile: SavedEQProfile): Result<Unit> {
        if (!shouldDisable && pendingProfile == profile && audioProcessors.isNotEmpty()) return Result.success(Unit)
        if (audioProcessors.isEmpty()) {
            Timber.tag(TAG)
                .w("No audio processors set yet. Storing profile as pending: ${profile.name}")
            pendingProfile = profile
            shouldDisable = false
            return Result.success(Unit)
        }

        pendingProfile = profile // Keep it for future processors
        shouldDisable = false
        
        var success = true
        var lastError: Exception? = null

        audioProcessors.forEach { processor ->
            try {
                applyProfileToProcessor(processor, profile)
            } catch (e: Exception) {
                success = false
                lastError = e
            }
        }

        return if (success) Result.success(Unit) else Result.failure(lastError ?: Exception("Unknown error"))
    }

    private fun applyProfileToProcessor(processor: CustomEqualizerAudioProcessor, profile: SavedEQProfile) {
        val parametricEQ = ParametricEQ(
            preamp = profile.preamp,
            bands = profile.bands
        )
        processor.applyProfile(parametricEQ)
    }

    /**
     * Disable the equalizer (flat response)
     * If audio processor is not set, stores pending disable request
     */
    @OptIn(UnstableApi::class)
    fun disable() {
        if (audioProcessors.isEmpty()) {
            Timber.tag(TAG).w("No audio processors set yet. Storing disable as pending")
            shouldDisable = true
            pendingProfile = null
            return
        }

        shouldDisable = true // Keep state
        pendingProfile = null

        audioProcessors.forEach { processor ->
            try {
                processor.disable()
            } catch (e: Exception) {
                Timber.tag(TAG).e("Failed to disable equalizer: ${e.message}")
            }
        }
        Timber.tag(TAG).d("Equalizer disabled on all processors")
    }

    /**
     * Bit Perfect mode: force a flat passthrough on every processor without
     * touching the saved profile or [shouldDisable], so it restores exactly
     * as it was when Bit Perfect is turned back off.
     */
    fun setBitPerfectBypass(bypass: Boolean) {
        bitPerfectBypass = bypass
        audioProcessors.forEach { it.forceBypass = bypass }
    }

    fun isInitialized(): Boolean {
        return audioProcessors.isNotEmpty()
    }

    @OptIn(UnstableApi::class)
    fun isEnabled(): Boolean {
        return audioProcessors.any { it.isEnabled() }
    }

    fun getEqualizerInfo(): EqualizerInfo {
        return EqualizerInfo(
            supportsUnlimitedBands = true,
            maxBands = Int.MAX_VALUE,
            description = "Custom ExoPlayer AudioProcessor with biquad filters"
        )
    }

    /**
     * Release resources (not needed for AudioProcessor, but kept for API compatibility)
     */
    fun release() {
        // AudioProcessor is managed by ExoPlayer, we just clear our reference
        audioProcessors.clear()
        Timber.tag(TAG).d("Audio processor references cleared")
    }
}

/**
 * Information about equalizer capabilities
 */
data class EqualizerInfo(
    val supportsUnlimitedBands: Boolean,
    val maxBands: Int,
    val description: String
)
