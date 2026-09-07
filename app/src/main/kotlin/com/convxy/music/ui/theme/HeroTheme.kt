package com.convxy.music.ui.theme

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import com.convxy.music.constants.AppTextColorKey
import com.convxy.music.ui.component.LocalAppBackdrop
import com.convxy.music.ui.component.backdrop.Backdrop
import com.convxy.music.utils.rememberPreference

/** saturation floor shared with [AppleTokens.shiftedForContrast] (0.08f). */
internal fun Color.isHueless(): Boolean {
    val hsl = FloatArray(3)
    ColorUtils.colorToHSL(toArgb(), hsl)
    return hsl[1] < 0.08f
}

/**
 * Wraps [content] in a nested [MaterialTheme] whose colour scheme is derived from
 * the screen's own hero [tint], so every `MaterialTheme.colorScheme` read on that
 * screen agrees with the background plane.
 *
 * When [tint] is hueless (grey / black / near-white), the app-wide scheme is kept
 * intact — re-theming from a grey produces a strictly worse result.
 *
 * Also provides [LocalAppBackdrop], [LocalContentColor] and [LocalAccentTextColor]
 * so glass chrome and explicit colour references stay in sync.
 */
@Composable
fun HeroTintedContent(
    tint: Color,
    backdrop: Backdrop,
    content: @Composable () -> Unit,
) {
    val base = MaterialTheme.colorScheme
    val onDark = tint.luminance() <= 0.5f

    // Theme screen's explicit text color, when set, wins over the tint-derived
    // hero text colors below — same override vivimusicTheme's own flatText
    // already applies to plain MaterialTheme.colorScheme reads, extended to
    // this screen-local system so hero screens (Home, Search, Library) agree
    // with it too instead of always following their artwork tint.
    val (appTextColorInt) = rememberPreference(AppTextColorKey, defaultValue = 0)

    // Was `base.accentText(tint, onDark)` alone, so any screen under this — Album,
    // Artist, Library, Search, every playlist screen — that reads
    // MaterialTheme.colorScheme.onSurface/onSurfaceVariant/secondary directly
    // (rather than through LocalContentColor/LocalAccentTextColor below) fell back
    // to the hero-tint-derived color and ignored the global text color setting.
    // flatText is the same override vivimusicTheme applies at the root; it has to
    // be re-applied here because this MaterialTheme rebuilds the scheme from scratch.
    val scheme = remember(base, tint, onDark, appTextColorInt) {
        val tinted = base.accentText(tint, onDark)
        if (appTextColorInt != 0) tinted.flatText(Color(appTextColorInt)) else tinted
    }
    val contentColor = if (appTextColorInt != 0) Color(appTextColorInt) else AppleTokens.onColor(tint)
    val headingColor = if (appTextColorInt != 0) Color(appTextColorInt) else AppleTokens.onColorHeading(tint)

    if (tint.isHueless()) {
        androidx.compose.runtime.CompositionLocalProvider(
            LocalAppBackdrop provides backdrop,
            LocalContentColor provides contentColor,
            LocalAccentTextColor provides headingColor,
        ) {
            content()
        }
    } else {
        MaterialTheme(
            colorScheme = scheme,
            typography = MaterialTheme.typography,
            shapes = MaterialTheme.shapes,
        ) {
            androidx.compose.runtime.CompositionLocalProvider(
                LocalAppBackdrop provides backdrop,
                LocalContentColor provides contentColor,
                LocalAccentTextColor provides headingColor,
            ) {
                content()
            }
        }
    }
}
