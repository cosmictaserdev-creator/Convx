/*
 * Vendored from Kyant0/backdrop v2.0.0 (io.github.kyant0:backdrop)
 * https://github.com/Kyant0/backdrop — Copyright 2025 Kyant0, Apache License 2.0
 *
 * Vendored so the library ships as source with this app (binary AARs compiled
 * against older Compose broke at runtime) and to add a backdrop resolution
 * scale for cheaper effect rendering. KMP expect/actual declarations were
 * merged into this single Android source set. Package renamed accordingly.
 */
package com.nexamusic.app.ui.component.backdrop.backdrops

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.unit.Density
import com.nexamusic.app.ui.component.backdrop.Backdrop
import com.nexamusic.app.ui.component.backdrop.internal.InverseLayerScope

private val DefaultOnDraw: ContentDrawScope.() -> Unit = { drawContent() }

@Composable
fun rememberLayerBackdrop(
    graphicsLayer: GraphicsLayer = rememberGraphicsLayer(),
    onDraw: ContentDrawScope.() -> Unit = DefaultOnDraw
): LayerBackdrop {
    return remember(graphicsLayer, onDraw) {
        LayerBackdrop(graphicsLayer, onDraw)
    }
}

@Stable
class LayerBackdrop internal constructor(
    val graphicsLayer: GraphicsLayer,
    internal val onDraw: ContentDrawScope.() -> Unit
) : Backdrop {

    override val isCoordinatesDependent: Boolean = true

    internal var layerCoordinates: LayoutCoordinates? by mutableStateOf(null)

    /**
     * Monotonic counter bumped by [LayerBackdropModifier]'s node each time this
     * backdrop's content is re-recorded. Glass surfaces read it during their draw
     * pass to skip re-recording their own layers when the source is static (e.g.
     * a surface-local animation such as the rim highlight drift), which used to
     * re-capture the whole screen on every frame.
     *
     * Deliberately a plain field, NOT snapshot state. It is written during the
     * draw phase and read during the draw phase by every sampling glass surface;
     * as snapshot state that read registered a draw dependency, so the write at
     * the end of each frame invalidated every surface, which dirtied the root
     * layer, which re-ran this node's draw and bumped the counter again. That
     * loop redrew the whole app at the GPU's maximum rate forever, on every
     * screen, with nothing on screen moving — measured 394 draws in 5s on a
     * static settings list, with no recomposition, measure or layout in the
     * trace. Freshness does not depend on the invalidation: a surface's recorded
     * layer replays this backdrop's RenderNode by reference, so re-recording the
     * source is picked up without the surface re-recording anything.
     */
    internal var contentVersion: Int = 0
        private set

    /** Called by the recording node after a successful record. */
    internal fun contentRecorded() {
        contentVersion++
    }

    /**
     * Regions declared static THIS generation by [backdropStaticRegion] children,
     * in this backdrop's own coordinate space. Plain list, not snapshot state, for
     * the same draw-phase-write reason as [contentVersion]. Cleared and rebuilt by
     * [LayerBackdropNode.draw] every time the source re-records, so a stale
     * declaration from a since-removed or since-changed composable never lingers
     * past the generation it was made in.
     */
    internal val staticRegions = ArrayList<Rect>()

    /**
     * True if [rect] (in this backdrop's coordinate space) falls entirely inside a
     * single region declared static this generation. Deliberately checks a single
     * covering rect rather than the union of several: a target only partly covered
     * by static declarations is treated as unstable rather than pulled into
     * region-union math, so a miss only costs a skipped optimization, never a
     * stale pixel on a sampling glass surface.
     */
    internal fun isFullyStatic(rect: Rect): Boolean =
        staticRegions.any { region ->
            region.left <= rect.left && region.top <= rect.top &&
                region.right >= rect.right && region.bottom >= rect.bottom
        }

    /**
     * True once the source has been recorded at least once. Freezing before that would
     * leave sampling surfaces replaying an empty layer.
     */
    internal val hasRecording: Boolean get() = contentVersion > 0

    private var inverseLayerScope: InverseLayerScope? = null

    override fun DrawScope.drawBackdrop(
        density: Density,
        coordinates: LayoutCoordinates?,
        layerBlock: (GraphicsLayerScope.() -> Unit)?
    ) {
        val coordinates = coordinates ?: return
        val layerCoordinates = layerCoordinates ?: return
        withTransform({
            if (layerBlock != null) {
                with(obtainInverseLayerScope()) { inverseTransform(density, layerBlock) }
            }
            val offset =
                try {
                    layerCoordinates.localPositionOf(coordinates)
                } catch (_: Exception) {
                    // TODO: outer transformations lead to wrong position calculation
                    coordinates.positionInWindow() - layerCoordinates.positionInWindow()
                }
            translate(-offset.x, -offset.y)
        }) {
            drawLayer(graphicsLayer)
        }
    }

    private fun obtainInverseLayerScope(): InverseLayerScope {
        return inverseLayerScope?.apply { reset() }
            ?: InverseLayerScope().also { inverseLayerScope = it }
    }
}
