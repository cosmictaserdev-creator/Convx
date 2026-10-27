package com.convx.music.viewmodels

import android.content.Context
import android.graphics.Typeface
import android.net.Uri
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.convx.music.constants.LyricsFontKey
import com.convx.music.constants.LyricsCustomFontKey
import com.convx.music.constants.LyricsTextSizeKey
import com.convx.music.utils.dataStore
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

data class LyricsAppearanceState(
    val font: String = "default", val size: Float = 30f,
    val importing: Boolean = false, val error: String? = null, val customFont: String = "",
)

class LyricsAppearanceViewModel(private val context: Context) : ViewModel() {
    private val pendingSize = MutableStateFlow<Float?>(null)
    private val importing = MutableStateFlow(false)
    private val error = MutableStateFlow<String?>(null)
    val state = combine(context.dataStore.data, pendingSize, importing, error) { prefs, size, loading, error ->
        LyricsAppearanceState(prefs[LyricsFontKey] ?: "default", size ?: (prefs[LyricsTextSizeKey] ?: 30f), loading, error, prefs[LyricsCustomFontKey].orEmpty())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LyricsAppearanceState())

    fun selectFont(value: String) { viewModelScope.launch { context.dataStore.edit { it[LyricsFontKey] = value } } }
    fun resize(size: Float) { pendingSize.value = size.coerceIn(18f, 60f) }
    fun saveSize() {
        val size = pendingSize.value ?: return
        viewModelScope.launch {
            context.dataStore.edit { it[LyricsTextSizeKey] = size }
            if (pendingSize.value == size) pendingSize.value = null
        }
    }

    fun importFont(uri: Uri) {
        if (importing.value) return
        importing.value = true
        error.value = null
        viewModelScope.launch {
            try {
                val file = withContext(Dispatchers.IO) {
                    val directory = context.filesDir.resolve("lyrics-fonts").apply { mkdirs() }
                    val file = directory.resolve("custom-${System.currentTimeMillis()}.font")
                    try {
                        context.contentResolver.openInputStream(uri)?.use { input ->
                            file.outputStream().use { output ->
                                val buffer = ByteArray(8192)
                                var total = 0
                                while (true) {
                                    val count = input.read(buffer)
                                    if (count < 0) break
                                    total += count
                                    require(total <= 8 * 1024 * 1024) { "Choose a font smaller than 8 MB" }
                                    output.write(buffer, 0, count)
                                }
                            }
                        } ?: error("Unable to open this font")
                        require(file.length() > 0) { "This font file is empty" }
                        Typeface.createFromFile(file)
                        file
                    } catch (failure: Exception) { file.delete(); throw failure }
                }
                context.dataStore.edit { it[LyricsFontKey] = file.name; it[LyricsCustomFontKey] = file.name }
                withContext(Dispatchers.IO) {
                    file.parentFile?.listFiles()?.filter { it.name != file.name }?.forEach { it.delete() }
                }
            } catch (cancelled: CancellationException) { throw cancelled
            } catch (failure: Exception) {
                error.value = "Could not import this font. Choose a valid TTF or OTF file."
            } finally { importing.value = false }
        }
    }
}
