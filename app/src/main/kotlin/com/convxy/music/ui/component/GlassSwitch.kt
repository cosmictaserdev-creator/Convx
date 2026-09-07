/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.component

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
 * import com.convxy.music.ui.component.GlassSwitchCompat as Switch
 * ```
 *
 * [colors] is accepted and deliberately ignored: both renderings take their track
 * and accent from the glass config and the theme, so Material color roles have
 * nothing to apply to. [thumbContent] is ignored by [GlassSwitch] but honoured on
 * the liquid path — [LiquidToggle] grew an optional slot for it — so the
 * check/close icons the call sites pass do survive, drawn on top of the glass
 * thumb in a colour that reads on white in either theme.
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
    // Every preference switch in the app comes through here (Preference.kt aliases
    // this as `Switch`), so this is the routing point for whether a toggle gets
    // the catalog's LiquidToggle — thumb refracting the track it drags across —
    // or the house [GlassSwitch] rendering. [liquidSwitchEligible] holds the full
    // list of reasons to decline; the two that matter most are a null handler (a
    // read-out switch whose row owns the click, which LiquidToggle's drag detector
    // would eat) and a thumb icon or custom colours, which its solid capsule
    // cannot carry.
    //
    // This used to be a hard delegation to [GlassSwitch] with the refraction
    // dropped on cost grounds: measured on a Galaxy M34 while scrolling, the
    // Appearance screen recorded at 49ms/frame against 25ms for the Settings root,
    // which has no switches — about 5ms of display-list recording per visible
    // toggle, the largest per-frame cost found on any screen, bigger than the
    // app-wide backdrop capture (~4ms) and bigger than the nav bar and mini player
    // glass combined. That number is still true, and it is why the effect sits
    // behind [GlassComponent.SETTINGS_CONTROLS] (on by default, one tap to turn
    // off) rather than being unconditional.
    //
    // The other reason it was dropped has since been fixed properly: LiquidToggle
    // samples the backdrop it is handed, and on screens inside the recorded app
    // layer that is a RenderNode cycle. [LiquidGlassSwitch] resolves the outer
    // backdrop through [rememberOuterBackdropSampler], so it refracts the page only
    // where doing so cannot draw the surface into its own recording.
    LiquidGlassSwitch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        enabled = enabled,
        modifier = modifier,
        thumbContent = thumbContent,
        colors = colors,
    )
}
