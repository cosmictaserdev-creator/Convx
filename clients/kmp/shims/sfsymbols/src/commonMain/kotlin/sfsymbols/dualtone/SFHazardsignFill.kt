package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFHazardsignFill (dualtone)
 * Viewport: 26.6504 x 24.0723
 */
public val SfSymbols.Dualtone.SFHazardsignFill: ImageVector
    get() {
        if (_sFHazardsignFill != null) {
            return _sFHazardsignFill!!
        }
        _sFHazardsignFill = sfIcon(
            name = "Dualtone.SFHazardsignFill",
            viewportWidth = 26.6504f,
            viewportHeight = 24.0723f
        ) {
            addSfPath("M3.26172 23.8672L23.0176 23.8672C25.0586 23.8672 26.2891 22.4414 26.2891 20.6348C26.2891 20.0488 26.123 19.4434 25.8008 18.8867L15.9277 1.62109C15.3125 0.537109 14.2285 0 13.1445 0C12.0508 0 10.9766 0.537109 10.3613 1.62109L0.488281 18.8867C0.15625 19.4531 0 20.0488 0 20.6348C0 22.4414 1.23047 23.8672 3.26172 23.8672Z", fillAlpha = 0.2125f)
            addSfPath("M4.61914 20.5469C4.0918 20.5469 3.7793 20.0391 4.0625 19.5312L12.5684 4.62891C12.832 4.14062 13.4375 4.14062 13.7207 4.62891L22.2168 19.5312C22.4902 20.0195 22.1875 20.5469 21.6504 20.5469ZM6.88477 18.7305L19.3945 18.7305C19.5215 18.7305 19.5996 18.6133 19.5312 18.4863L13.2812 7.51953C13.2227 7.40234 13.0664 7.40234 13.0078 7.51953L6.74805 18.457C6.66992 18.6035 6.75781 18.7305 6.88477 18.7305Z", fillAlpha = 0.85f)
        }
        return _sFHazardsignFill!!
    }

private var _sFHazardsignFill: ImageVector? = null
