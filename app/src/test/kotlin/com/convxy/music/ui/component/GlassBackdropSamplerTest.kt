package com.convxy.music.ui.component

import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.unit.Density
import com.convxy.music.ui.component.backdrop.Backdrop
import com.convxy.music.ui.component.backdrop.backdrops.emptyBackdrop
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Test

/**
 * Unit tests for the backdrop-selection half of [OuterBackdropSampler].
 *
 * The ancestry walk itself needs real [LayoutCoordinates] from a laid-out tree,
 * which a JVM test cannot produce; what is tested here is the decision that walk
 * feeds, plus the cases where no coordinates are involved at all. Those are the
 * cases that decide whether a glass surface samples a layer it is being recorded
 * into — a RenderNode cycle that ends in a native SIGSEGV, not a visual defect —
 * so the direction of every default matters.
 */
class GlassBackdropSamplerTest {

    /** A backdrop that is live (not the sentinel) but records nothing here. */
    private object FakeBackdrop : Backdrop {
        override val isCoordinatesDependent: Boolean = false
        override fun DrawScope.drawBackdrop(
            density: Density,
            coordinates: LayoutCoordinates?,
            layerBlock: (GraphicsLayerScope.() -> Unit)?
        ) = Unit
    }

    @Test
    fun `a live backdrop is sampled when it is safe to`() {
        assertSame(FakeBackdrop, outerBackdropFor(FakeBackdrop, safeToSample = true))
    }

    @Test
    fun `a live backdrop is dropped when sampling it would be a cycle`() {
        assertSame(emptyBackdrop(), outerBackdropFor(FakeBackdrop, safeToSample = false))
    }

    @Test
    fun `nothing provided is never reported as live`() {
        assertSame(emptyBackdrop(), outerBackdropFor(null, safeToSample = true))
        assertSame(emptyBackdrop(), outerBackdropFor(emptyBackdrop(), safeToSample = true))
    }

    @Test
    fun `safety is never granted without coordinates proving it`() {
        // No backdrop, the sentinel, and a backdrop that is not a recorded layer
        // all answer false rather than throwing — the surface just renders
        // frosted, which is the cheap path and the safe one.
        assertFalse((null as Backdrop?).recordsAncestorOf(null))
        assertFalse(emptyBackdrop().recordsAncestorOf(null))
        assertFalse(FakeBackdrop.recordsAncestorOf(null))
    }
}
