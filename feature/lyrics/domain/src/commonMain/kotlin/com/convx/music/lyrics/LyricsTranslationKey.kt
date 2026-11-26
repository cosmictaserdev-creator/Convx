package com.convx.music.lyrics

/** Identifies a translation by its actual input, so hash collisions cannot reuse the wrong lyrics. */
data class LyricsTranslationKey(val text: String, val mode: String, val language: String)
