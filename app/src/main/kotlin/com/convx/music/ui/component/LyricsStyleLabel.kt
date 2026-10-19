package com.convx.music.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.convx.music.R
import com.convx.music.constants.LyricsAnimationStyle

@Composable
fun lyricsStyleLabel(style: LyricsAnimationStyle): String = when (style) {
    LyricsAnimationStyle.FLAMINGO -> stringResource(R.string.lyrics_style_flamingo)
    LyricsAnimationStyle.NONE -> stringResource(R.string.none)
    LyricsAnimationStyle.FADE -> stringResource(R.string.fade)
    LyricsAnimationStyle.GLOW -> stringResource(R.string.glow)
    LyricsAnimationStyle.SLIDE -> stringResource(R.string.slide)
    LyricsAnimationStyle.KARAOKE -> stringResource(R.string.karaoke)
    LyricsAnimationStyle.APPLE -> stringResource(R.string.apple_music_style)
    LyricsAnimationStyle.APPLE_V2 -> stringResource(R.string.apple_music_style_letter)
    LyricsAnimationStyle.BITCHORD -> stringResource(R.string.lyrics_animation_bitchord)
    LyricsAnimationStyle.CONVX_1 -> stringResource(R.string.convx_1)
    LyricsAnimationStyle.LYRICS_V2 -> stringResource(R.string.lyrics_v2_fluid)
    LyricsAnimationStyle.METRO_LYRICS -> stringResource(R.string.lyrics_animation_metro)
    LyricsAnimationStyle.ARCHIVE_TUNE -> stringResource(R.string.lyrics_animation_karaoke_apple)
}
