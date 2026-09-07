/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.component

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.convxy.music.constants.AppBackgroundColorKey
import com.convxy.music.constants.HomeBackgroundAnimateKey
import com.convxy.music.constants.HomeBackgroundBlurKey
import com.convxy.music.constants.HomeBackgroundDimKey
import com.convxy.music.constants.HomeBackgroundEnabledKey
import com.convxy.music.constants.HomeBackgroundIsVideoKey
import com.convxy.music.constants.HomeBackgroundPathKey
import com.convxy.music.constants.HomeBackgroundQualityKey
import com.convxy.music.utils.rememberPreference
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import java.io.File

/** Absolute ceiling on the background's stored/decoded resolution, regardless
 *  of the quality preference or how large the device's own screen is — keeps
 *  an exotic tablet/foldable from decoding an unreasonably large bitmap. */
private const val BackgroundAbsoluteMaxEdge = 2560

/**
 * The size a background image is stored at (HomeBackgroundSettings.kt's
 * copyBackgroundMedia) and decoded at (this file, below): the device's own
 * screen resolution — so "full quality" actually means full quality on every
 * device, not a fixed 1080x1920 that under-serves a QHD+ screen — scaled by
 * [quality] (0.3..1) and clamped to [BackgroundAbsoluteMaxEdge]. Shared by
 * both the store-time and display-time requests so they always agree.
 */
@Composable
fun rememberHomeBackgroundTargetSize(quality: Float): Pair<Int, Int> {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    return remember(configuration, density, quality) {
        val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
        val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }
        val q = quality.coerceIn(0.3f, 1f)
        val width = (screenWidthPx * q).toInt().coerceIn(1, BackgroundAbsoluteMaxEdge)
        val height = (screenHeightPx * q).toInt().coerceIn(1, BackgroundAbsoluteMaxEdge)
        width to height
    }
}

/** Process-wide: the intro blur ramp plays only the first time this session. */
private var blurAnimatedThisSession = false

/**
 * True when the user has set a background of their own — an image, or a flat colour.
 *
 * Screens that draw their own backdrop (blurred album art, theme wash) must check
 * this and suppress those layers — otherwise they render underneath the custom
 * background along with their scrims, and the user's choice comes out looking nothing
 * like it does on a screen that has no backdrop of its own.
 *
 * A picked colour counts here, not just an image: it used to test the image path
 * alone, so a colour-only setup fell through to the auto-generated hero blur on the
 * search-results page and never showed the colour at all.
 */
@Composable
fun hasCustomHomeBackground(): Boolean {
    val (enabled) = rememberPreference(HomeBackgroundEnabledKey, false)
    val (path) = rememberPreference(HomeBackgroundPathKey, "")
    val (colorInt) = rememberPreference(AppBackgroundColorKey, 0)
    return (enabled && path.isNotEmpty()) || colorInt != 0
}

/**
 * The colour a screen should paint under everything, resolving the two preferences that
 * both claim to set "the background" in one place:
 *
 * 1. an explicit background colour ([AppBackgroundColorKey]) if the user picked one,
 * 2. otherwise the theme colour when dynamic theming is off (what
 *    [com.convxy.music.ui.component.rememberAppBackgroundTint] has always done),
 * 3. otherwise [fallback] — each screen's own default surface.
 *
 * Home, Library and both search screens previously each resolved this differently and
 * disagreed on the default: `colorScheme.background` on Home, `colorScheme.primary` on
 * Library, `AppleTokens.BgElevated` on Search.
 */
@Composable
fun rememberAppBackgroundColor(fallback: Color): Color {
    val (colorInt) = rememberPreference(AppBackgroundColorKey, 0)
    return if (colorInt != 0) Color(colorInt) else rememberAppBackgroundTint(fallback)
}

/**
 * The user's custom home background image (blurred + dimmed), shared by the Home and
 * Library screens. Draws nothing when disabled or unset. Must be placed as a layer
 * behind the screen content inside a [BoxScope] (uses [matchParentSize]).
 *
 * Blur is always the live RenderEffect (Modifier.blur), including once the intro
 * ramp settles. A prior version swapped in a bitmap pre-blurred by successive
 * downscale/upscale at that point to save the per-frame GPU cost — cheaper, but
 * the resample approximation showed visible seams/blocking versus a real blur.
 *
 * A version before that tried baking it into the decoded bitmap via a Coil
 * Transformation and rendered unblurred on-device regardless of
 * algorithm/cache-key/hardware-bitmap fixes.
 *
 * @param withGradient adds the bottom primary-color wash on top of the image.
 * @param contentLoaded when animate is on, the blur eases in once this flips true
 *   (i.e. when the screen's content items appear), not when the image itself loads.
 */
@Composable
fun BoxScope.HomeImageBackground(
    withGradient: Boolean = false,
    contentLoaded: Boolean = true,
) {
    val (enabled) = rememberPreference(HomeBackgroundEnabledKey, false)
    val (path) = rememberPreference(HomeBackgroundPathKey, "")
    val (blur) = rememberPreference(HomeBackgroundBlurKey, 20f)
    val (dim) = rememberPreference(HomeBackgroundDimKey, 0.4f)
    val (animate) = rememberPreference(HomeBackgroundAnimateKey, false)
    val (isVideo) = rememberPreference(HomeBackgroundIsVideoKey, false)
    if (!enabled || path.isEmpty()) {
        // No custom image set — paint the user's chosen plain background
        // color if they set one (0 = unset, draws nothing, exactly like
        // before this preference existed).
        val (backgroundColorInt) = rememberPreference(AppBackgroundColorKey, 0)
        if (backgroundColorInt != 0) {
            Box(modifier = Modifier.matchParentSize().background(Color(backgroundColorInt)))
        }
        return
    }

    if (isVideo) {
        // No blur-in animation for video — the loop is already motion, ramping
        // blur on top of it double-animates for no benefit.
        HomeVideoBackground(path = path, blur = blur, dim = dim, withGradient = withGradient)
        return
    }

    // The intro blur ramp plays once per app session, not on every navigation to a screen
    // with this background. `appeared` starts already-true when it has run before, so the
    // animateFloatAsState inits straight at the blur target (static, no re-animation).
    val shouldAnimate = animate && !blurAnimatedThisSession
    var appeared by remember { mutableStateOf(!shouldAnimate) }
    LaunchedEffect(shouldAnimate) {
        if (shouldAnimate) {
            appeared = true
            blurAnimatedThisSession = true
        }
    }
    val animatedBlur by animateFloatAsState(
        targetValue = if (appeared && contentLoaded) blur else 0f,
        // Long, gentle ease-out (easeOutExpo-like) for a fluid, unhurried settle.
        animationSpec = tween(
            durationMillis = 2200,
            easing = CubicBezierEasing(0.16f, 1f, 0.3f, 1f),
        ),
        label = "homeBgBlur",
    )
    val effectiveBlur = if (animate) animatedBlur else blur
    val context = LocalContext.current

    // Was a flat 1080x1920 request regardless of what the image was actually
    // stored at — so even after raising the stored resolution to the
    // device's own screen size (HomeBackgroundSettings.kt), this decode step
    // downsampled it right back down on every render, on any screen bigger
    // than 1080x1920. Match the same quality-scaled target used at store time.
    val (quality) = rememberPreference(HomeBackgroundQualityKey, 1f)
    val (targetWidth, targetHeight) = rememberHomeBackgroundTargetSize(quality)
    val imageRequest = remember(path, targetWidth, targetHeight) {
        ImageRequest.Builder(context)
            .data(File(path))
            .size(targetWidth, targetHeight)
            .crossfade(false)
            .build()
    }

    // Always the live RenderEffect blur — a prior version swapped in a bitmap
    // pre-blurred via successive downscale/upscale once "settled" (cheaper per
    // frame while scrolling), but that resample approximation showed visible
    // seams/blocking versus a real blur. Simplicity + correctness over the
    // per-frame GPU cost for a background that doesn't otherwise animate.
    AsyncImage(
        model = imageRequest,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .matchParentSize()
            .blur(effectiveBlur.dp),
    )
    Box(
        modifier = Modifier
            .matchParentSize()
            .background(Color.Black.copy(alpha = dim)),
    )
    if (withGradient) {
        val primary = MaterialTheme.colorScheme.primary
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        0.55f to Color.Transparent,
                        1f to primary.copy(alpha = 0.55f),
                    ),
                ),
        )
    }
}
