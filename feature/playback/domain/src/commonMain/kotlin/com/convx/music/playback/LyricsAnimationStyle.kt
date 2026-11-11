package com.convx.music.playback

import kotlinx.serialization.Serializable

@Serializable
enum class LyricsAnimationStyle {
    FLAMINGO,
    NONE,
    FADE,
    GLOW,
    SLIDE,
    KARAOKE,
    APPLE,
    APPLE_V2,
    BITCHORD,
    CONVX_1,
    LYRICS_V2,
    METRO_LYRICS,

    /**
     * the reference app's treatment: Apple Music's karaoke view, rendered by
     * `com.mocharealm.accompanist:lyrics-ui`. The only style that animates the whole
     * column rather than one line at a time, which is what the smooth catch-up scroll,
     * the depth-of-field falloff and duet (left/right) alignment all need.
     */
    ARCHIVE_TUNE,

    /**
     * Word-by-word kinetic stack under a convex lens (after Verci's Fisheye): the sung word large
     * in the middle, earlier words stacked above. Only when the lyrics have word timings.
     */
    FISHEYE,
}
