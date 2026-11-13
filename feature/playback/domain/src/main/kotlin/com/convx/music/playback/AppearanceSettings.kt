package com.convx.music.playback

import kotlinx.serialization.Serializable

@Serializable
data class AppearanceSettings(
    val accentArgb: Long = 0xFFFA233B,
    val lightBackgroundArgb: Long? = null,
    val darkBackgroundArgb: Long? = null,
    val lightTextArgb: Long? = null,
    val darkTextArgb: Long? = null,
    val followSystemTheme: Boolean = false,
    val fontFamily: String = "System",
    val customFontFile: String? = null,
    val fontScale: Float = 1f,
    val sidebarWidth: Float = 260f,
    val containerCornerRadius: Float = 16f,
    val artworkCornerRadius: Float = 14f,
    val glassEnabled: Boolean = true,
    val glassBlurRadius: Float = 16f,
    val glassLensAmount: Float = 12f,
    val glassOpacity: Float = 0.38f,
    val glassSaturation: Float = 1.8f,
    val glassChromaticAberration: Boolean = false,
    val glassDepthEffect: Boolean = false,
    val showHomeFilters: Boolean = false,
) {
    fun normalized() = copy(
        accentArgb = opaque(accentArgb),
        lightBackgroundArgb = lightBackgroundArgb?.let(::opaque), darkBackgroundArgb = darkBackgroundArgb?.let(::opaque),
        lightTextArgb = lightTextArgb?.let(::opaque), darkTextArgb = darkTextArgb?.let(::opaque),
        fontScale = fontScale.valid(1f, 0.8f, 1.4f), sidebarWidth = sidebarWidth.valid(260f, 220f, 320f),
        containerCornerRadius = containerCornerRadius.valid(16f, 0f, 24f), artworkCornerRadius = artworkCornerRadius.valid(14f, 0f, 60f),
        glassBlurRadius = glassBlurRadius.valid(16f, 0f, 40f), glassLensAmount = glassLensAmount.valid(12f, 0f, 40f),
        glassOpacity = glassOpacity.valid(0.38f, 0.1f, 0.9f), glassSaturation = glassSaturation.valid(1.8f, 0f, 3f),
        fontFamily = fontFamily.trim().take(120).ifBlank { "System" },
    )

    companion object {
        private fun opaque(value: Long) = (value and 0xFFFFFF) or 0xFF000000
        private fun Float.valid(fallback: Float, min: Float, max: Float) = if (isFinite()) coerceIn(min, max) else fallback
    }
}
