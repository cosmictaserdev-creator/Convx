/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.component

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.SwitchColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import com.convxy.music.ui.component.backdrop.Backdrop
import com.convxy.music.ui.component.backdrop.catalog.components.LiquidToggle
import com.convxy.music.ui.component.backdrop.isRenderEffectSupported

/**
 * Whether a switch may render as the library's [LiquidToggle] instead of the
 * house [GlassSwitch]. Pulled out as a plain function because it is a decision
 * with several independent reasons to say no, and every one of them has bitten
 * before.
 *
 * - `!componentEnabled` — the user turned settings glass off.
 * - `!glassAllowed` — below Android 12, or a low-RAM device.
 * - `!enabled` — LiquidToggle has no disabled state; GlassSwitch does.
 * - `!hasHandler` — a null handler means the switch is a read-out and the row
 *   around it owns the click. The liquid path swallows taps so the row cannot
 *   double-fire (see the consumer below), which would leave that row dead, and
 *   the thumb would animate against a value that never changes.
 * - `translucentFallback` — the user chose the cheap style, or the platform
 *   cannot do a real blur; a "glass" thumb with nothing behind it is a white dot.
 *
 * [hasThumbContent] and [hasColors] are in the signature but deliberately take no
 * part in the decision. Both are already ignored on the fallback path — see
 * [GlassSwitchCompat], whose own doc says the glass switch draws its thumb and
 * takes its track from the config, so the icons and Material colour roles call
 * sites pass "have nothing to apply to". Declining the liquid path for them
 * therefore preserved nothing, and it cost the effect almost everywhere:
 * `SwitchPreference`, the row behind most settings toggles, passes a check/close
 * icon, so gating on the icon left every one of those rows on the old switch.
 * They stay in the signature so that stays testable.
 */
@Suppress("UNUSED_PARAMETER")
internal fun liquidSwitchEligible(
    componentEnabled: Boolean,
    glassAllowed: Boolean,
    enabled: Boolean,
    hasHandler: Boolean,
    hasThumbContent: Boolean,
    hasColors: Boolean,
    translucentFallback: Boolean,
): Boolean = componentEnabled && glassAllowed && enabled && hasHandler &&
        !translucentFallback

/**
 * A settings switch rendered as the liquid glass toggle from the vendored
 * Android Liquid Glass catalog — the same `LiquidToggle` the library ships in its
 * demo app: a track recorded into its own layer, and a thumb that refracts it
 * through a combined backdrop, with blur trading down to lens as the press
 * deepens, a squash on velocity and an ambient rim that appears under the finger.
 *
 * It is safe to put anywhere, including deep inside the recorded app layer, which
 * is what makes it usable on settings screens at all. The outer backdrop is
 * resolved through [rememberOuterBackdropSampler]: where the surface sits inside
 * the node recording that backdrop — every settings screen, the search overlay —
 * it is dropped and the thumb refracts only its own track. The track is recorded
 * by a sibling, so that half is always legal.
 *
 * Falls back to [GlassSwitch] (which falls back to a plain iOS-shaped toggle when
 * glass is unavailable) for every case [liquidSwitchEligible] rejects.
 *
 * Cost, measured when this was first tried: roughly 5ms of display-list recording
 * per visible toggle, the largest per-frame cost found on any screen. It is
 * therefore behind [GlassComponent.SETTINGS_CONTROLS], on by default.
 */
@Composable
fun LiquidGlassSwitch(
    checked: Boolean,
    // Nullable for the same reason it is on [GlassSwitch]: null is a read-out, the
    // surrounding row owns the click.
    onCheckedChange: ((Boolean) -> Unit)?,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    // [thumbContent] is honoured — LiquidToggle grew an optional slot for it, the
    // one local addition to the vendored component. [colors] is accepted and
    // ignored, exactly as [GlassSwitchCompat] documents: the toggle takes its
    // track and accent from the theme the way the library's does, so Material
    // colour roles have nothing to apply to. Both stay in the signature so a call
    // site can move between the switches by changing an import, the same trick the
    // settings screens use for `Switch` and `Slider`.
    thumbContent: (@Composable () -> Unit)? = null,
    colors: SwitchColors? = null,
    backdrop: Backdrop? = LocalAppBackdrop.current,
) {
    val config = LocalGlassEffectConfig.current
    val eligible = liquidSwitchEligible(
        componentEnabled = config.isEnabledFor(GlassComponent.SETTINGS_CONTROLS),
        glassAllowed = isGlassAllowed(),
        enabled = enabled,
        hasHandler = onCheckedChange != null,
        hasThumbContent = thumbContent != null,
        hasColors = colors != null,
        translucentFallback = shouldUseTranslucentGlassFallback(
            config.style,
            isRenderEffectSupported(),
        ),
    )

    if (!eligible) {
        GlassSwitch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            modifier = modifier,
        )
        return
    }

    // The thumb is an opaque white capsule at rest and clears to glass as the
    // press deepens, so an icon drawn on it needs a colour that reads on both —
    // and on white in either theme. The call sites pass a bare Icon() with no
    // tint, which resolves to LocalContentColor: white on white in dark mode,
    // which is what Material's Switch avoided by colouring the slot itself.
    // Greens are the toggle's own accent, so a checked switch reads as one icon
    // over one material rather than two unrelated colours.
    val isDark = isSystemInDarkTheme()
    val thumbIconColor =
        if (checked) {
            if (isDark) Color(0xFF30D158) else Color(0xFF34C759)
        } else {
            Color(0xFF787880)
        }
    // Explicit composable type on the inner val: a lambda literal returned
    // straight out of `let` has to have its composable-ness inferred through the
    // generic, and getting that wrong is a compile error at the call to content().
    val thumbSlot: (@Composable () -> Unit)? = thumbContent?.let { content ->
        val slot: @Composable () -> Unit = {
            CompositionLocalProvider(LocalContentColor provides thumbIconColor) {
                content()
            }
        }
        slot
    }

    val outer = rememberOuterBackdropSampler(backdrop)
    LiquidToggle(
        selected = { checked },
        onSelect = { newValue -> onCheckedChange?.invoke(newValue) },
        backdrop = outer.effective,
        thumbContent = thumbSlot,
        modifier = modifier
            .semantics(mergeDescendants = true) {
                // LiquidToggle marks the thumb Role.Switch but carries no state,
                // so the switch would announce itself without saying which way it
                // is set. The click action is for TalkBack: it fires without a
                // synthesized tap, so it does not depend on the drag detector.
                role = Role.Switch
                toggleableState = if (checked) ToggleableState.On else ToggleableState.Off
                onClick {
                    onCheckedChange?.invoke(!checked)
                    true
                }
            }
            // LiquidToggle reads the down event on the Initial pass with
            // requireUnconsumed = false and never consumes anything — that is what
            // lets the same detector serve a tap and a drag. It also means a
            // settings row that is itself clickable still fires underneath, which
            // is invisible where the row's click is the same toggle and wrong
            // where it is not. Consume here instead: the Main pass runs child
            // first, so the thumb has already had the gesture, and moves are left
            // alone so dragging the thumb still works.
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown().consume()
                    waitForUpOrCancellation()?.consume()
                }
            }
            .then(outer.measureModifier),
    )
}
