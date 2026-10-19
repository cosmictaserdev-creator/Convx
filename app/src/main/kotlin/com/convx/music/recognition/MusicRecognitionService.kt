/**
 * Music Recognition Feature
 * 
 * This feature is based on the original MusicRecognizer project by Aleksey Saenko.
 * Original project: https://github.com/aleksey-saenko/MusicRecognizer
 * 
 * Special thanks to Aleksey Saenko for the music recognition implementation.
 */

package com.convx.music.recognition

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.content.ContextCompat
import com.music.shazamkit.Shazam
import com.music.shazamkit.models.RecognitionStatus
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.nio.ByteOrder

/**
 * Service for recognizing music using audio fingerprinting.
 * Records audio from the microphone, generates a Shazam-compatible fingerprint,
 * and sends it to the Shazam API for recognition.
 */
object MusicRecognitionService {
    
    private const val RECORDING_SAMPLE_RATE = 44100
    private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
    private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    // Use the original recognizer's final 10-second fallback duration.
    private const val RECORDING_DURATION_MS = 10000L
    
    private val _recognitionStatus = MutableStateFlow<RecognitionStatus>(RecognitionStatus.Ready)
    val recognitionStatus: StateFlow<RecognitionStatus> = _recognitionStatus.asStateFlow()
    
    fun hasRecordPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context, 
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    /**
     * Start the music recognition process.
     * Records audio, generates fingerprint, and queries Shazam API.
     */
    @SuppressLint("MissingPermission")
    suspend fun recognize(context: Context): RecognitionStatus = withContext(Dispatchers.IO) {
        if (!hasRecordPermission(context)) {
            return@withContext RecognitionStatus.Error("Microphone permission not granted")
        }
        
        _recognitionStatus.value = RecognitionStatus.Listening
        
        try {
            val audioData = recordAudio()
            currentCoroutineContext().ensureActive()
            
            _recognitionStatus.value = RecognitionStatus.Processing
            
            // Resample to mono 16kHz for fingerprinting.
            val decodedAudio = DecodedAudio(
                data = audioData,
                channelCount = 1,
                sampleRate = RECORDING_SAMPLE_RATE,
                pcmEncoding = AUDIO_FORMAT
            )
            
            val resampledAudio = AudioResampler.resample(
                decodedAudio, 
                VibraSignature.REQUIRED_SAMPLE_RATE
            ).getOrElse { error ->
                _recognitionStatus.value = RecognitionStatus.Error("Failed to resample audio: ${error.message}")
                return@withContext _recognitionStatus.value
            }
            
            require(
                resampledAudio.channelCount == 1 &&
                resampledAudio.sampleRate == VibraSignature.REQUIRED_SAMPLE_RATE &&
                resampledAudio.pcmEncoding == AudioFormat.ENCODING_PCM_16BIT &&
                ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN &&
                resampledAudio.data.isNotEmpty() && 
                resampledAudio.data.size % 2 == 0
            ) { "Invalid audio format for fingerprint generation" }
            
            // Fingerprint with native library.
            val signature = try {
                VibraSignature.fromI16(resampledAudio.data)
            } catch (e: Exception) {
                _recognitionStatus.value = RecognitionStatus.Error("Failed to generate fingerprint: ${e.message}")
                return@withContext _recognitionStatus.value
            }
            
            val sampleDurationMs = (resampledAudio.data.size / 2) * 1000L / VibraSignature.REQUIRED_SAMPLE_RATE
            
            val result = Shazam.recognize(signature, sampleDurationMs)
            
            result.fold(
                onSuccess = { recognitionResult ->
                    _recognitionStatus.value = RecognitionStatus.Success(recognitionResult)
                },
                onFailure = { error ->
                    val message = error.message ?: "Unknown error"
                    _recognitionStatus.value = if (message.contains("No match", ignoreCase = true)) {
                        RecognitionStatus.NoMatch("No matches found. Try again with clearer audio.")
                    } else {
                        RecognitionStatus.Error(message)
                    }
                }
            )
            
            _recognitionStatus.value
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            _recognitionStatus.value = RecognitionStatus.Error(e.message ?: "Recognition failed")
            _recognitionStatus.value
        }
    }
    
    @SuppressLint("MissingPermission")
    private suspend fun recordAudio(): ByteArray = withContext(Dispatchers.IO) {
        val bufferSize = AudioRecord.getMinBufferSize(
            RECORDING_SAMPLE_RATE, 
            CHANNEL_CONFIG, 
            AUDIO_FORMAT
        )
        if (bufferSize <= 0) error("Invalid audio buffer size: $bufferSize")
        val audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            RECORDING_SAMPLE_RATE,
            CHANNEL_CONFIG,
            AUDIO_FORMAT,
            bufferSize
        )
        if (audioRecord.state != AudioRecord.STATE_INITIALIZED) {
            audioRecord.release()
            error("Microphone could not be initialized")
        }
        
        val outputStream = ByteArrayOutputStream()
        val buffer = ByteArray(bufferSize)
        val startTime = android.os.SystemClock.elapsedRealtime()
        
        try {
            audioRecord.startRecording()
            
            while (android.os.SystemClock.elapsedRealtime() - startTime < RECORDING_DURATION_MS && isActive) {
                val bytesRead = audioRecord.read(buffer, 0, bufferSize)
                if (bytesRead > 0) {
                    outputStream.write(buffer, 0, bytesRead)
                } else if (bytesRead < 0) {
                    error("Microphone recording failed: $bytesRead")
                }
            }
        } finally {
            runCatching { audioRecord.stop() }
            audioRecord.release()
        }
        
        currentCoroutineContext().ensureActive()
        outputStream.toByteArray().also { require(it.isNotEmpty()) { "No audio was recorded" } }
    }
    
    fun reset() {
        _recognitionStatus.value = RecognitionStatus.Ready
    }
}
