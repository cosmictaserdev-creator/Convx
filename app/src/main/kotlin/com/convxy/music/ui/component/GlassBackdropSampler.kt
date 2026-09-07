/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import com.convxy.music.ui.component.backdrop.Backdrop
import com.convxy.music.ui.component.backdrop.backdrops.LayerBackdrop
import com.convxy.music.ui.component.backdrop.backdrops.emptyBackdrop

/**
 * True when the layer this backdrop records is an ancestor of [coordinates].
 *
 * Sampling a backdrop from inside the node that records it draws that node's own
 * content into the surface that is about to be drawn into it — a RenderNode
 * reference cycle. The engine has no guard against it: it fails in
 * `RenderNode::prepareTreeImpl` with a native SIGSEGV, which takes the process
 * down rather than mis-drawing. Every glass surface on a screen therefore has to
 * know whether the backdrop it was handed is recording a layer it lives inside.
 *
 * An unattached [LayerBackdrop] — the house pattern for "glass material with
 * nothing behind it", used by `DiyPlayerMockup`, `DiyEditorScreen` and
 * `HideOnScrollFAB` — never records anything, so it is never an ancestor and this
 * returns false for it.
 */
fun Backdrop?.recordsAncestorOf(coordinates: LayoutCoordinates?): Boolean {
    val layer = this as? LayerBackdrop ?: return false
    val source = layer.layerCoordinates ?: return false
    // The surface itself (or its own modifier layer) sampling its own recording is
    // circular even when that recording is paint-only.
    if (coordinates === source || coordinates?.parentLayoutCoordinates === source) {
        return true
    }
    // A drawBackdrop export holds the surface's own paint and nothing below it, so
    // a recording made this way cannot contain the sampler: nested glass — a sheet
    // handing its surface to the controls inside — is legal by construction. Only
    // whole-subtree recordings (Modifier.layerBackdrop) are circular from within.
    if (layer.recordsOwnPaintOnly) return false
    // From the surface itself, not its parent: a modifier on the recording node
    // reports that node's own coordinates, and a surface sampling from there is
    // just as circular as one sampling from inside it.
    //
    // `parentCoordinates` is the fine-grained chain — parent layout modifier, then
    // parent layout — so a walk from a descendant passes through each of an
    // ancestor's modifier layers and hits whichever one did the recording. The
    // layout-only chain is checked alongside it: identity is the whole test here,
    // and a false negative is a crash rather than a mis-draw. (Compose renamed
    // `parent` to these two; there is no `findCommonParent` left to lean on.)
    var node: LayoutCoordinates? = coordinates
    while (node != null) {
        if (node === source) return true
        if (node.parentLayoutCoordinates === source) return true
        node = node.parentCoordinates
    }
    return false
}

/**
 * The backdrop a glass surface should actually sample: the one it was handed when
 * that is both live and safe, and [emptyBackdrop] — which draws nothing — when it
 * is not. Split out from [OuterBackdropSampler] because it is a pure decision and
 * the sampler needs `LayoutCoordinates`, which a unit test cannot build.
 */
internal fun outerBackdropFor(raw: Backdrop?, safeToSample: Boolean): Backdrop =
    if (safeToSample && raw.isLiveGlassBackdrop()) raw!! else emptyBackdrop()

/**
 * Decides, per layout pass, whether the backdrop a glass surface inherited may be
 * sampled from where that surface sits — and hands out the backdrop to use instead
 * when it may not.
 *
 * Install [measureModifier] on the surface that does the sampling and read
 * [effective] where the backdrop is needed:
 *
 * ```
 * val outer = rememberOuterBackdropSampler()
 * Box(Modifier.then(outer.measureModifier)) {
 *     drawBackdrop(outer.effective, ...)
 * }
 * ```
 *
 * Surfaces built this way are safe anywhere in the tree. Inside the recorded root
 * — settings screens, the search overlay, a player preview — they fall back to the
 * frosted pipeline with nothing to refract; next to the recorded content — the real
 * player sheet over the nav host, chrome over a hero backdrop — they sample it.
 * Components that carry their own small backdrop, like a slider thumb over its
 * track, keep refracting that regardless, because it is recorded by a sibling.
 *
 * Safety starts out false and is only granted by the first layout pass that proves
 * it, so there is no frame in which an unproven backdrop gets sampled.
 */
@Stable
class OuterBackdropSampler internal constructor(private val raw: Backdrop?) {

    var safeToSample by mutableStateOf(false)
        private set

    /** The backdrop to sample. Never the sentinel unless sampling is unsafe. */
    val effective: Backdrop
        get() = outerBackdropFor(raw, safeToSample)

    /** Install on the sampling surface. Cheap: a parent walk on reposition only. */
    val measureModifier: Modifier = Modifier.onGloballyPositioned { coordinates ->
        val safe = !raw.recordsAncestorOf(coordinates)
        if (safe != safeToSample) safeToSample = safe
    }
}

@Composable
fun rememberOuterBackdropSampler(
    backdrop: Backdrop? = LocalAppBackdrop.current,
): OuterBackdropSampler = remember(backdrop) { OuterBackdropSampler(backdrop) }
