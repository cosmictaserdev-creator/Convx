package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFHazardsignFill (monochrome)
 * Viewport: 26.6504 x 24.0723
 */
public val SfSymbols.Monochrome.SFHazardsignFill: ImageVector
    get() {
        if (_sFHazardsignFill != null) {
            return _sFHazardsignFill!!
        }
        _sFHazardsignFill = sfIcon(
            name = "Monochrome.SFHazardsignFill",
            viewportWidth = 26.6504f,
            viewportHeight = 24.0723f
        ) {
            addSfPath("M15.9277 1.62109L25.8008 18.8867C26.123 19.4434 26.2891 20.0488 26.2891 20.6348C26.2891 22.4414 25.0586 23.8672 23.0176 23.8672L3.26172 23.8672C1.23047 23.8672 0 22.4414 0 20.6348C0 20.0488 0.15625 19.4531 0.488281 18.8867L10.3613 1.62109C10.9766 0.537109 12.0508 0 13.1445 0C14.2285 0 15.3125 0.537109 15.9277 1.62109ZM12.5684 4.62891L4.0625 19.5312C3.7793 20.0391 4.0918 20.5469 4.61914 20.5469L21.6504 20.5469C22.1875 20.5469 22.4902 20.0195 22.2168 19.5312L13.7207 4.62891C13.4375 4.14062 12.832 4.14062 12.5684 4.62891ZM13.2812 7.51953L19.5312 18.4863C19.5996 18.6133 19.5215 18.7305 19.3945 18.7305L6.88477 18.7305C6.75781 18.7305 6.66992 18.6035 6.74805 18.457L13.0078 7.51953C13.0664 7.40234 13.2227 7.40234 13.2812 7.51953Z", fillAlpha = 0.85f)
        }
        return _sFHazardsignFill!!
    }

private var _sFHazardsignFill: ImageVector? = null
