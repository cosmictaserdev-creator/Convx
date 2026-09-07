package com.convxy.music.ui.utils

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Indication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer

/**
 * Springy "wobble" on press for an existing [interactionSource]: the target scales down
 * while held and overshoots back on release (low damping = bouncy). Use on components that
 * already own an interaction source — nav bar items, glass buttons/pills — where
 * [bounceClick] can't wrap the click. Icon/label only; doesn't affect layout.
 */
fun Modifier.pressWobble(
    interactionSource: InteractionSource,
    pressedScale: Float = 0.86f,
) = composed {
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) pressedScale else 1f,
        animationSpec = spring(
            dampingRatio = 0.38f,
            stiffness = Spring.StiffnessMedium,
        ),
        label = "pressWobble",
    )
    graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

/*
 * The two click modifiers below used to add a spring scale-down on press. That is
 * gone. It was built on `composed {}`, which the Compose compiler cannot skip and
 * which defeats modifier reuse, and each instance additionally allocated a
 * MutableInteractionSource, started a collectIsPressedAsState flow collector,
 * started an animateFloatAsState, and added a graphicsLayer — per item, rebuilt
 * every time a row recycled. With ~200 call sites, nearly all of them inside lazy
 * lists, that was the app's largest per-frame scroll cost. The reference app this
 * one is compared against uses plain combinedClickable and has no `composed {}`
 * anywhere.
 *
 * These stay as named wrappers rather than being deleted so the ~200 call sites,
 * and the option of reintroducing the press feedback, both stay in one place. To
 * bring the bounce back, implement it as a Modifier.Node — not with `composed`.
 */

/** Clickable with no ripple by default. */
fun Modifier.bounceClick(
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    indication: Indication? = null,
    onClick: () -> Unit
): Modifier = clickable(
    interactionSource = interactionSource,
    indication = indication,
    enabled = enabled,
    onClick = onClick
)

/** [combinedClickable] with no ripple by default. */
fun Modifier.combinedBounceClick(
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    indication: Indication? = null,
    onLongClick: (() -> Unit)? = null,
    onDoubleClick: (() -> Unit)? = null,
    onClick: () -> Unit
): Modifier = combinedClickable(
    interactionSource = interactionSource,
    indication = indication,
    enabled = enabled,
    onLongClick = onLongClick,
    onDoubleClick = onDoubleClick,
    onClick = onClick
)
