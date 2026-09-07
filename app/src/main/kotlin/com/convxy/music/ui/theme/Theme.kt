/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.theme

import android.graphics.Bitmap
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontFamily
import androidx.palette.graphics.Palette
import com.convxy.music.constants.AppFont
import com.convxy.music.constants.AppTextColorKey
import com.convxy.music.constants.SelectedFontKey
import com.convxy.music.utils.rememberPreference
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.rememberDynamicColorScheme
import com.materialkolor.score.Score

val DefaultThemeColor = AppleTokens.AccentRed

/**
 * The user's chosen accent color, provided app-wide. ONLY the nav bar uses this
 * as an accent; the rest of the UI is contrast-based (white/black by luminance).
 * Kept separate from [ColorScheme.primary], which is neutralized to white in the
 * dark Apple theme so generic M3 controls don't render red.
 */
val LocalAccentColor = androidx.compose.runtime.compositionLocalOf { DefaultThemeColor }

/**
 * The accent tuned for readable body text: same hue, lightened on dark
 * backgrounds and darkened on light ones, but never pushed to near-white or
 * near-black — so it reads as "coloured text," legible on either theme.
 * Consume via [LocalAccentTextColor].
 */
fun accentTextColor(accent: Color, dark: Boolean): Color {
    val blended = if (dark) {
        androidx.compose.ui.graphics.lerp(accent, Color.White, 0.35f)
    } else {
        androidx.compose.ui.graphics.lerp(accent, Color.Black, 0.45f)
    }
    // A very dark or very pale accent can survive the blend and still be
    // unreadable against the surface, so clamp lightness rather than trust it.
    val hsl = FloatArray(3)
    androidx.core.graphics.ColorUtils.colorToHSL(blended.toArgb(), hsl)
    hsl[2] = if (dark) hsl[2].coerceAtLeast(0.72f) else hsl[2].coerceAtMost(0.32f)
    return Color(androidx.core.graphics.ColorUtils.HSLToColor(hsl))
}

/** Accent-contrast text color, provided app-wide from the current accent + theme. */
val LocalAccentTextColor = androidx.compose.runtime.compositionLocalOf { DefaultThemeColor }

@Composable
fun vivimusicTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    pureBlack: Boolean = true,
    themeColor: Color = DefaultThemeColor,
    content: @Composable () -> Unit,
) {
    val baseColorScheme = rememberDynamicColorScheme(
        seedColor = themeColor,
        isDark = darkTheme,
        specVersion = ColorSpec.SpecVersion.SPEC_2025,
        style = PaletteStyle.TonalSpot,
    )

    val (appTextColorInt) = rememberPreference(AppTextColorKey, defaultValue = 0)

    val colorScheme = remember(baseColorScheme, pureBlack, darkTheme, themeColor, appTextColorInt) {
        val withApple = if (darkTheme) baseColorScheme.appleSurfaces() else baseColorScheme
        val withBlack = if (darkTheme && pureBlack) withApple.pureBlack(true) else withApple
        val withAccentText = withBlack.accentText(themeColor, darkTheme)
        // Theme screen's explicit text color, when set, replaces the accent-tinted
        // text roles everywhere they're read from MaterialTheme.colorScheme — this
        // is the single root override every screen's default Text() picks up, same
        // mechanism accentText itself uses. Fill/control roles (primary, onPrimary)
        // are left alone; only text roles change.
        if (appTextColorInt != 0) withAccentText.flatText(Color(appTextColorInt)) else withAccentText
    }

    val (selectedFont) = rememberPreference(SelectedFontKey, defaultValue = AppFont.SYSTEM.value)
    val customFont = rememberCustomFontFamily()

    val appFont = remember(selectedFont, customFont) {
        when (AppFont.fromValue(selectedFont)) {
            AppFont.SYSTEM -> customFont ?: FontFamily.Default
            AppFont.GOOGLE_SANS -> GoogleSansFontFamily
            AppFont.SANS_FLEX -> SansFlexFontFamily
            AppFont.OUTFIT -> OutfitFontFamily
            AppFont.PLUS_JAKARTA_SANS -> PlusJakartaSansFontFamily
        }
    }

    androidx.compose.runtime.CompositionLocalProvider(
        LocalAccentColor provides themeColor,
        // Same override as flatText just above: the user's explicit text color,
        // when set, wins over the accent-derived one — this is what the shared
        // top bar (wordmark + every screen's title, including Settings) actually
        // reads, since it sits outside any screen's own theme scope.
        LocalAccentTextColor provides (
            if (appTextColorInt != 0) Color(appTextColorInt) else accentTextColor(themeColor, darkTheme)
        ),
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = remember(appFont) { AppTypography(appFont) },
            shapes = AppShapes,
            content = content
        )
    }
}

fun Bitmap.extractThemeColor(): Color {
    val swatches = Palette.from(this)
        .maximumColorCount(8)
        .generate()
        .swatches

    // Material's Score falls back to Google Blue (0xff4285f4) when no swatch is
    // chromatic enough to seed a scheme — which is exactly what a black and white
    // cover produces. The result was a blue theme for a greyscale sleeve. Detect
    // that case up front and seed with the artwork's own grey instead, so a
    // monochrome cover yields a monochrome theme.
    if (swatches.isAchromatic()) return neutralSeed(swatches)

    val rankedColors = Score.score(swatches.associate { it.rgb to it.population })
    return Color(rankedColors.first())
}

/** True when the artwork carries no usable hue. Shares the tested rule in
 *  [com.convxy.music.ui.theme.PlayerColorExtractor.isAchromatic]. */
private fun List<Palette.Swatch>.isAchromatic(): Boolean {
    if (isEmpty()) return true
    val hsv = FloatArray(3)
    val saturations = FloatArray(size)
    val values = FloatArray(size)
    val populations = IntArray(size)
    forEachIndexed { i, swatch ->
        android.graphics.Color.colorToHSV(swatch.rgb, hsv)
        saturations[i] = hsv[1]
        values[i] = hsv[2]
        populations[i] = swatch.population
    }
    return PlayerColorExtractor.isAchromatic(saturations, values, populations)
}

/** Strips hue from [argb] and pins its brightness, for greyscale gradients. */
private fun desaturate(argb: Int, value: Float): Color {
    val hsv = FloatArray(3)
    android.graphics.Color.colorToHSV(argb, hsv)
    hsv[1] = 0f
    hsv[2] = value
    return Color(android.graphics.Color.HSVToColor(hsv))
}

/**
 * A mid grey taken from the artwork's own dominant tone. Used as a scheme seed for
 * greyscale covers: a pure neutral would give Material nothing to build a tonal
 * palette from, so keep a trace of chroma while staying visually grey.
 */
private fun neutralSeed(swatches: List<Palette.Swatch>): Color {
    val dominant = swatches.maxByOrNull { it.population }?.rgb ?: return Color(0xFF6E6E6E)
    val hsv = FloatArray(3)
    android.graphics.Color.colorToHSV(dominant, hsv)
    hsv[1] = 0.04f
    hsv[2] = hsv[2].coerceIn(0.35f, 0.70f)
    return Color(android.graphics.Color.HSVToColor(hsv))
}

/**
 * Grabs one frame from a canvas video URL and runs the same extraction as
 * [Bitmap.extractThemeColor] on it — canvas videos are often more colorful/
 * representative than the still cover art. Best-effort: HLS (.m3u8) frame
 * extraction isn't reliable on every device/Android version, so callers
 * should fall back to the static artwork on a null result. Blocking — call
 * from a background dispatcher.
 */
fun extractThemeColorFromVideoFrame(videoUrl: String): Color? {
    val retriever = android.media.MediaMetadataRetriever()
    return try {
        retriever.setDataSource(videoUrl, emptyMap())
        // A second or so in tends to avoid a black/fade-in first frame.
        val frame = retriever.getFrameAtTime(1_000_000L, android.media.MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
        frame?.extractThemeColor()
    } catch (e: Exception) {
        null
    } finally {
        retriever.release()
    }
}

fun Bitmap.extractGradientColors(): List<Color> {
    val swatches = Palette.from(this)
        .maximumColorCount(64)
        .generate()
        .swatches

    // Same Google-Blue fallback trap as extractThemeColor: on greyscale artwork
    // Score returns 0xff4285f4 and the gradient comes out blue. Hand back greys
    // drawn from the image instead.
    if (swatches.isAchromatic()) {
        val ordered = swatches.sortedByDescending { Color(it.rgb).luminance() }
        val light = ordered.firstOrNull()?.rgb?.let { desaturate(it, 0.62f) } ?: Color(0xFF595959)
        val dark = ordered.lastOrNull()?.rgb?.let { desaturate(it, 0.16f) } ?: Color(0xFF0D0D0D)
        return listOf(light, dark)
    }

    val extractedColors = swatches.associate { it.rgb to it.population }

    val orderedColors = Score.score(extractedColors, 2, 0xff4285f4.toInt(), true)
        .sortedByDescending { Color(it).luminance() }

    return if (orderedColors.size >= 2)
        listOf(Color(orderedColors[0]), Color(orderedColors[1]))
    else
        listOf(Color(0xFF595959), Color(0xFF0D0D0D))
}

/**
 * Apple-dark surface overrides applied unconditionally in dark mode.
 * pureBlack is applied AFTER this and wins for surface/background when enabled.
 */
fun ColorScheme.appleSurfaces() = copy(
    background = AppleTokens.Bg,
    surface = AppleTokens.Bg,
    surfaceContainerLowest = AppleTokens.Bg,
    surfaceContainerLow = AppleTokens.BgElevated,
    surfaceContainer = AppleTokens.Card,
    surfaceContainerHigh = AppleTokens.Card,
    surfaceContainerHighest = AppleTokens.CardSecondary,
    surfaceVariant = AppleTokens.CardSecondary,
    // primary/onPrimary set here are only a base — accentText runs afterwards and
    // gives them the contrast-clamped accent, so controls and link text carry the
    // theme colour rather than rendering flat white.
    primary = Color.White,
    onPrimary = Color.Black,
    primaryContainer = AppleTokens.CardSecondary,
    onPrimaryContainer = Color.White,
)

/**
 * Swaps Material's neutral text roles for accent-contrast ones, so every screen
 * picks it up from `MaterialTheme.colorScheme` without being edited.
 *
 * Body text keeps only a whisper of the accent — it has to stay readable over
 * long lists — while secondary text carries it plainly, which is what replaces
 * M3's washed-out grey. Tune the two lerp amounts to taste; they are the whole
 * calibration.
 */
fun ColorScheme.accentText(accent: Color, dark: Boolean): ColorScheme {
    val body = if (dark) {
        androidx.compose.ui.graphics.lerp(accent, Color.White, 0.86f)
    } else {
        androidx.compose.ui.graphics.lerp(accent, Color.Black, 0.86f)
    }
    val secondary = accentTextColor(accent, dark)
    return copy(
        onSurface = body,
        onBackground = body,
        onSurfaceVariant = secondary,
        onSecondaryContainer = secondary,
        onTertiaryContainer = secondary,
        onPrimaryContainer = secondary,
        onSecondary = secondary,
        // The subtitle role: the artist name under a song or album title reads
        // through this everywhere in the app.
        secondary = secondary,
        // `primary` is the app's second-largest text/icon role after
        // onSurfaceVariant — links, selected states, section actions. appleSurfaces
        // had pinned it to flat white in dark mode, which is most of what still
        // read as untinted Material. It doubles as a fill (buttons, sliders), so
        // onPrimary flips to the opposite extreme to keep those legible.
        primary = secondary,
        onPrimary = if (dark) Color.Black else Color.White,
    )
}

/**
 * Flattens every text role [accentText] tints to one explicit user-chosen color —
 * the Theme screen's "Text Color" override.
 *
 * `primary` is included, and it has to be: [accentText] deliberately routes the app's
 * second-largest text role through it (links, "See All" actions, selected states,
 * every settings screen's highlighted label), so leaving it accent-derived meant a
 * user who picked a text color still saw red text across Settings, About and section
 * actions. It doubles as a button/slider fill, so `onPrimary` is derived from the
 * picked color's own luminance rather than the theme's — a dark text color picked in
 * dark mode would otherwise leave black-on-dark button labels.
 */
fun ColorScheme.flatText(color: Color) = copy(
    onSurface = color,
    onBackground = color,
    onSurfaceVariant = color,
    onSecondaryContainer = color,
    onTertiaryContainer = color,
    onPrimaryContainer = color,
    onSecondary = color,
    secondary = color,
    primary = color,
    onPrimary = if (color.luminance() > 0.5f) Color.Black else Color.White,
)

fun ColorScheme.pureBlack(apply: Boolean) =
    if (apply) copy(
        surface = Color.Black,
        background = Color.Black
    ) else this

val ColorSaver = object : Saver<Color, Int> {
    override fun restore(value: Int): Color = Color(value)
    override fun SaverScope.save(value: Color): Int = value.toArgb()
}
