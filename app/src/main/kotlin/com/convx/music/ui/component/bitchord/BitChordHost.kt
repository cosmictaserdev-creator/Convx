/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 *
 * The slice of BitChord's player host the lyrics panel talks to. Convx fills it in where the panel is used.
 * Ported from BitChord (GPL-3.0). See NOTICE.
 */
package com.convx.music.ui.component.bitchord

import com.convx.music.lyrics.bitchord.LyricLine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed interface LyricsTranslationResult {
    data class Translated(val lines: List<LyricLine>) : LyricsTranslationResult
    data class SameLanguage(val language: String) : LyricsTranslationResult
    data object Unavailable : LyricsTranslationResult
}

sealed interface LyricsRomanizationResult {
    data class Romanized(val lines: List<LyricLine>) : LyricsRomanizationResult
    data object AlreadyRomanized : LyricsRomanizationResult
    data object Unavailable : LyricsRomanizationResult
}

/** The player settings the lyrics panel reads. */
class PlayerSettingsSource {
    val lyricsBlur: StateFlow<Boolean> = MutableStateFlow(true)
    val lyricsOffsetMs: StateFlow<Int> = MutableStateFlow(0)
    val reduceAnimation: StateFlow<Boolean> = MutableStateFlow(false)
    val reduceDynamicBlur: StateFlow<Boolean> = MutableStateFlow(false)
    val translationLanguage: StateFlow<String> = MutableStateFlow("en")

    internal fun update(
        blur: Boolean = lyricsBlur.value,
        offsetMs: Int = lyricsOffsetMs.value,
        reduceAnimation: Boolean = this.reduceAnimation.value,
        reduceDynamicBlur: Boolean = this.reduceDynamicBlur.value,
        translationLanguage: String = this.translationLanguage.value,
    ) {
        (lyricsBlur as MutableStateFlow).value = blur
        (lyricsOffsetMs as MutableStateFlow).value = offsetMs
        (this.reduceAnimation as MutableStateFlow).value = reduceAnimation
        (this.reduceDynamicBlur as MutableStateFlow).value = reduceDynamicBlur
        (this.translationLanguage as MutableStateFlow).value = translationLanguage
    }
}

/** What the panel asks of the app around it. */
interface PlayerHost {
    val settings: PlayerSettingsSource

    suspend fun translateLyrics(trackId: String, lines: List<LyricLine>, targetLanguageTag: String): LyricsTranslationResult =
        LyricsTranslationResult.Unavailable

    suspend fun romanizeLyrics(trackId: String, lines: List<LyricLine>, targetLanguageTag: String): LyricsRomanizationResult =
        LyricsRomanizationResult.Unavailable

    fun showMessage(message: String) {}
}

object PlayerPlatform {
    private val defaultHost = object : PlayerHost {
        override val settings = PlayerSettingsSource()
    }

    /** Replaced by the app so translation and messages reach Convx's own services. */
    @Volatile
    var host: PlayerHost = defaultHost
}

internal val PlayerSettings: PlayerSettingsSource
    get() = PlayerPlatform.host.settings
