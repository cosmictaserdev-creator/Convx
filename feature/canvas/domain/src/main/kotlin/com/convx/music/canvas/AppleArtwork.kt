package com.convx.music.canvas

import kotlinx.serialization.Serializable

/** One Apple Music image plus the colours Apple's servers computed for it (0xRRGGBB, null when absent). */
@Serializable
data class AppleArtwork(
    val url: String,
    val width: Int = 0,
    val height: Int = 0,
    val bgColor: Int? = null,
    val textColor1: Int? = null,
    val textColor2: Int? = null,
    val textColor3: Int? = null,
    val textColor4: Int? = null,
) {
    /** Concrete URL for a [px]-wide request. Apple scales server-side, capped at the source size.
     * [png] keeps transparency for logos; some templates hard-code a `.jpg` suffix.
     */
    fun sized(px: Int, png: Boolean = false): String {
        val w = if (width > 0) minOf(px, width) else px
        val h = if (width > 0 && height > 0) w * height / width else w
        val concrete = url.replace("{w}", w.toString()).replace("{h}", h.toString()).replace("{f}", if (png) "png" else "jpg")
        return if (png) concrete.replace(Regex("""\.jpg$"""), ".png") else concrete
    }
}
