package com.convx.music.playback.presentation

/** Positive offsets advance the lyric clock; seeking applies the inverse adjustment. */
fun lyricDisplayPosition(playbackPositionMs: Long, offsetMs: Long): Long =
    (playbackPositionMs + offsetMs).coerceAtLeast(0L)

fun lyricSeekPosition(lineTimeMs: Long, offsetMs: Long): Long =
    (lineTimeMs - offsetMs).coerceAtLeast(0L)
