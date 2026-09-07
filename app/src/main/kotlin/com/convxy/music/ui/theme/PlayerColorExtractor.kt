/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.palette.graphics.Palette
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Player color extraction system for generating gradients from album artwork
 * 
 * This system analyzes album artwork and extracts vibrant, dominant colors
 * to create visually appealing gradients for the music player interface.
 */
object PlayerColorExtractor {

    /**
     * Fallback for artwork Palette can't extract a usable color from — a solid
     * black/white/grayscale cover, mostly. NOT a theme color: falling back to
     * MaterialTheme.colorScheme.primaryContainer painted every neutral cover
     * the theme's accent color (blue on the M3 baseline scheme) regardless of
     * the actual artwork, which read as visibly wrong.
     */
    val NeutralFallbackColor = Color(0xFF1C1C1E)

    /**
     * Extracts colors from a palette and creates a gradient
     * 
     * @param palette The color palette extracted from album artwork
     * @param fallbackColor Fallback color to use if extraction fails
     * @return List of colors for gradient (primary, darker variant, black)
     */
    suspend fun extractGradientColors(
        palette: Palette,
        fallbackColor: Int
    ): List<Color> = withContext(Dispatchers.Default) {

        // Extract all available colors with priority for dominant colors
        val colorCandidates = listOfNotNull(
            palette.dominantSwatch, // High priority for dominant color
            palette.vibrantSwatch,
            palette.darkVibrantSwatch,
            palette.lightVibrantSwatch,
            palette.mutedSwatch,
            palette.darkMutedSwatch,
            palette.lightMutedSwatch
        )

        // Select best color based on weight (dominance + vibrancy)
        val bestSwatch = colorCandidates.maxByOrNull { calculateColorWeight(it) }
        val fallbackDominant = palette.dominantSwatch?.rgb?.let { Color(it) }
            ?: Color(palette.getDominantColor(fallbackColor))

        val primaryColor = when {
            // A greyscale cover has to come out greyscale. Every path below this
            // multiplies saturation, and on a near-neutral image that amplifies
            // whatever few degrees of colour cast the JPEG happens to carry into a
            // decisive tint — a black and white photograph came out green or
            // magenta depending on the encoder. Neutralize instead of amplify.
            isArtworkAchromatic(palette) -> neutralToneOf(bestSwatch?.rgb?.let(::Color) ?: fallbackDominant)

            bestSwatch != null && isColorVibrant(Color(bestSwatch.rgb)) ->
                enhanceColorVividness(Color(bestSwatch.rgb), VIBRANT_SATURATION_FACTOR)

            else -> enhanceColorVividness(fallbackDominant, FALLBACK_SATURATION_FACTOR)
        }

        // Create sophisticated gradient with 3 color points
        listOf(
            primaryColor, // Start: primary vibrant color
            primaryColor.copy(
                red = (primaryColor.red * 0.6f).coerceAtLeast(0f),
                green = (primaryColor.green * 0.6f).coerceAtLeast(0f),
                blue = (primaryColor.blue * 0.6f).coerceAtLeast(0f)
            ), // Middle: darker version of primary color
            Color.Black // End: black
        )
    }

    /**
     * True when the artwork carries no meaningful hue — greyscale photography, a
     * black sleeve, a plain white one, a pencil drawing.
     *
     * Judged across the whole palette weighted by population rather than from a
     * single swatch: Palette will happily hand back a "vibrant" swatch made of
     * twelve stray pixels, and picking on vibrancy alone is exactly how a
     * monochrome cover ends up tinted. If the picture is overwhelmingly neutral,
     * a small saturated region does not make it a coloured picture.
     */
    private fun isArtworkAchromatic(palette: Palette): Boolean {
        val hsv = FloatArray(3)
        val saturations = FloatArray(palette.swatches.size)
        val values = FloatArray(palette.swatches.size)
        val populations = IntArray(palette.swatches.size)
        palette.swatches.forEachIndexed { i, swatch ->
            android.graphics.Color.colorToHSV(swatch.rgb, hsv)
            saturations[i] = hsv[1]
            values[i] = hsv[2]
            populations[i] = swatch.population
        }
        return isAchromatic(saturations, values, populations)
    }

    /**
     * The achromatic decision itself, over raw HSV samples — split out from
     * [isArtworkAchromatic] so it is reachable from a plain JVM test, where
     * `android.graphics.Color` is not.
     *
     * @return true when the population-weighted mean saturation is below
     *   [ACHROMATIC_SATURATION_THRESHOLD]. An empty palette counts as achromatic:
     *   no evidence of colour is not evidence of colour.
     */
    internal fun isAchromatic(
        saturations: FloatArray,
        values: FloatArray,
        populations: IntArray,
    ): Boolean {
        var weighted = 0.0
        var total = 0.0
        for (i in saturations.indices) {
            // Saturation is meaningless at the extremes of value — near-black and
            // near-white pixels report erratic hue. Weight them out rather than
            // letting them decide.
            val valueWeight = (values[i] * (1f - values[i]) * 4f).coerceIn(0f, 1f)
            val weight = populations[i].toDouble() * valueWeight
            weighted += saturations[i] * weight
            total += weight
        }
        if (total <= 0.0) return true
        return weighted / total < ACHROMATIC_SATURATION_THRESHOLD
    }

    /**
     * Strips the hue from [color], keeping its lightness. The result is the grey
     * the artwork actually is, rather than a grey chosen in advance — a charcoal
     * sleeve and a newsprint one still read differently.
     */
    private fun neutralToneOf(color: Color): Color {
        val hsv = FloatArray(3)
        android.graphics.Color.colorToHSV(color.toArgb(), hsv)
        hsv[1] = 0f
        hsv[2] = hsv[2].coerceIn(NEUTRAL_VALUE_MIN, NEUTRAL_VALUE_MAX)
        return Color(android.graphics.Color.HSVToColor(hsv))
    }

    /** Mean population-weighted saturation below which artwork counts as greyscale. */
    private const val ACHROMATIC_SATURATION_THRESHOLD = 0.10f

    // Greyscale artwork keeps a wider value range than a tinted one: there is no
    // hue doing the work, so the tone itself has to carry the difference between a
    // black sleeve and a white one.
    private const val NEUTRAL_VALUE_MIN = 0.14f
    private const val NEUTRAL_VALUE_MAX = 0.72f

    private const val VIBRANT_SATURATION_FACTOR = 1.12f
    private const val FALLBACK_SATURATION_FACTOR = 1.0f

    /**
     * Determines if a color is vibrant enough for use in player UI
     * 
     * @param color The color to analyze
     * @return true if the color has sufficient saturation and brightness
     */
    private fun isColorVibrant(color: Color): Boolean {
        val argb = color.toArgb()
        val hsv = FloatArray(3)
        android.graphics.Color.colorToHSV(argb, hsv)
        val saturation = hsv[1] // HSV[1] is saturation
        val brightness = hsv[2] // HSV[2] is brightness
        
        // Color is vibrant if it has sufficient saturation and appropriate brightness
        // Avoid colors that are too dark or too bright
        return saturation > 0.25f && brightness > 0.2f && brightness < 0.9f
    }
    
    /**
     * Enhances color vividness by adjusting saturation and brightness
     * 
     * @param color The color to enhance
     * @param saturationFactor Factor to multiply saturation by (default 1.4)
     * @return Enhanced color with improved vividness
     */
    private fun enhanceColorVividness(color: Color, saturationFactor: Float = 1.4f): Color {
        val argb = color.toArgb()
        val hsv = FloatArray(3)
        android.graphics.Color.colorToHSV(argb, hsv)

        // Nudge saturation rather than push it. The old 1.3-1.4x made every cover
        // arrive at roughly the same poster-paint intensity, which is what read as
        // "not what Apple does" — theirs stays recognisably the colour of the
        // sleeve. Cap below full so nothing lands on a pure primary.
        hsv[1] = (hsv[1] * saturationFactor).coerceAtMost(0.82f)
        // Value floor was 0.4, which lifted every dark cover to a mid tone and lost
        // the distinction between a black sleeve and a bright one. Wide range, and
        // only the top end is pulled in — a background still has to stay behind
        // white text.
        hsv[2] = (hsv[2] * 0.92f).coerceIn(0.12f, 0.70f)

        return Color(android.graphics.Color.HSVToColor(hsv))
    }

    /**
     * Calculates weight for color selection based on dominance and vibrancy
     * 
     * @param swatch The palette swatch to analyze
     * @return Weight value for color selection priority
     */
    private fun calculateColorWeight(swatch: Palette.Swatch?): Float {
        if (swatch == null) return 0f
        val population = swatch.population.toFloat()
        val color = Color(swatch.rgb)
        val argb = color.toArgb()
        val hsv = FloatArray(3)
        android.graphics.Color.colorToHSV(argb, hsv)
        val saturation = hsv[1]
        val brightness = hsv[2]
        
        // Give higher priority to dominance (population) while considering vibrancy
        val populationWeight = population * 2f // Double dominance weight
        val vibrancyBonus = if (saturation > 0.3f && brightness > 0.3f) 1.5f else 1f
        
        return populationWeight * vibrancyBonus * (saturation + brightness) / 2f
    }

    /**
     * Configuration constants for color extraction
     */
    object Config {
        const val MAX_COLOR_COUNT = 32
        const val BITMAP_AREA = 8000
        const val IMAGE_SIZE = 200
        
        // Color enhancement factors
        const val VIBRANT_SATURATION_THRESHOLD = 0.25f
        const val VIBRANT_BRIGHTNESS_MIN = 0.2f
        const val VIBRANT_BRIGHTNESS_MAX = 0.9f
        
        const val POPULATION_WEIGHT_MULTIPLIER = 2f
        const val VIBRANCY_THRESHOLD_SATURATION = 0.3f
        const val VIBRANCY_THRESHOLD_BRIGHTNESS = 0.3f
        const val VIBRANCY_BONUS = 1.5f
        
        const val DEFAULT_SATURATION_FACTOR = 1.4f
        const val VIBRANT_SATURATION_FACTOR = 1.3f
        const val FALLBACK_SATURATION_FACTOR = 1.1f
        
        const val BRIGHTNESS_MULTIPLIER = 0.9f
        const val BRIGHTNESS_MIN = 0.4f
        const val BRIGHTNESS_MAX = 0.85f
        
        const val DARKER_VARIANT_FACTOR = 0.6f
    }
}
