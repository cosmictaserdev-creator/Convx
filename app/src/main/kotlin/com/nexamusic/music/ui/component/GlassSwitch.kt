/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.nexamusic.app.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SwitchColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

/**
 * iOS-style toggle switch with liquid glass track when glass is enabled.
 * Falls back to solid green/grey track on unsupported devices.
 */
@Composable
fun GlassSwitch(
    checked: Boolean,
    // Nullable, and it has to stay that way down the whole chain: null means "this
    // switch is a read-out, the row around it owns the click". Wrapping a null in a
    // non-null lambda still installs a clickable, which eats the touch and calls
    // nothing — the switch looks live and does nothing when tapped.
    onCheckedChange: ((Boolean) -> Unit)?,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val glassConfig = LocalGlassEffectConfig.current
    val useGlass = glassConfig.globalEnabled && isGlassAllowed()

    val thumbProgress by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = tween(200),
        label = "glassSwitchThumb",
    )

    val trackShape = RoundedCornerShape(16.dp)
    val trackWidth = 51.dp
    val trackHeight = 31.dp
    val thumbSize = 27.dp
    val thumbPadding = 2.dp
    val maxTravel = trackWidth - thumbSize - thumbPadding * 2

    // No liquidGlass here, deliberately. It used to run the full backdrop
    // pipeline on an UNATTACHED backdrop — which makes drawBackdrop early-return,
    // so there was never any live content to refract. The old comment admitted as
    // much ("glass alone rendered as a faint outline, 'on' was all but
    // indistinguishable from 'off'"), which is why a green wash was painted over
    // the top to restore the on-state signal.
    //
    // So every enabled switch paid for layer allocation, an effect chain, a
    // highlight and a shadow to produce a faint rim over a colour that was
    // covering it anyway. Measured on a Galaxy M34: the Appearance screen (~5
    // switches on) recorded at 49ms/frame against 25ms for the Settings root,
    // which has none — roughly 5ms per switch, on a 51x31dp control. Preference.kt
    // routes every preference switch in the app through here, so that cost was
    // on most settings screens at once.
    //
    // The rim is reproduced with a plain border below: same read, no pipeline.
    Box(
        modifier = modifier
            .width(trackWidth)
            .height(trackHeight)
            .clip(trackShape)
            .background(
                when {
                    !enabled -> Color(0xFF39393D).copy(alpha = 0.4f)
                    checked -> Color(0xFF34C759)
                    else -> Color(0xFF39393D)
                }
            )
            .then(
                if (useGlass && checked && enabled) {
                    // The specular hint the glass rim used to contribute.
                    Modifier.border(
                        width = 0.8.dp,
                        color = Color.White.copy(alpha = 0.35f),
                        shape = trackShape,
                    )
                } else {
                    Modifier
                }
            )
            .then(
                if (onCheckedChange != null) {
                    Modifier.clickable(enabled = enabled) { onCheckedChange(!checked) }
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .offset { IntOffset((thumbPadding + maxTravel * thumbProgress).roundToPx(), 0) }
                .size(thumbSize)
                .shadow(3.dp, CircleShape)
                .clip(CircleShape)
                .background(Color.White),
        )
    }
}

/**
 * Signature-compatible stand-in for Material3's `Switch`, so a screen can adopt
 * [GlassSwitch] by aliasing its import rather than rewriting every call site:
 *
 * ```
 * import com.nexamusic.app.ui.component.GlassSwitchCompat as Switch
 * ```
 *
 * [thumbContent] and [colors] are accepted and deliberately ignored — the glass
 * switch draws its own thumb and takes its track from the glass config, so the
 * check/close icons and Material color roles the call sites pass have nothing to
 * apply to.
 */
@Composable
fun GlassSwitchCompat(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    thumbContent: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    colors: SwitchColors? = null,
) {
    // Delegates to [GlassSwitch] rather than LiquidToggle. LiquidToggle attaches
    // its own backdrop to the track so the thumb can refract it — a real glass
    // pipeline per toggle, and every preference switch in the app comes through
    // here (Preference.kt aliases this as `Switch`).
    //
    // Measured on a Galaxy M34, scrolling: the Appearance screen recorded at
    // 49ms/frame against 25ms for the Settings root, which has no switches —
    // about 5ms of display-list recording per visible toggle, for refraction on a
    // 51x31dp control. That was the single largest per-frame cost found on any
    // screen, larger than the app-wide backdrop capture (~4ms) and larger than
    // the nav bar and mini player glass combined.
    //
    // Visual change, stated plainly: the thumb no longer refracts the track. The
    // toggle keeps its iOS shape, motion, green on-state and rim highlight.
    GlassSwitch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        enabled = enabled,
        modifier = modifier,
    )
}
