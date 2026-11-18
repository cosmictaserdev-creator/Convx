package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDiamondRighthalfFilled (monochrome)
 * Viewport: 28.6035 x 28.2642
 */
public val SfSymbols.Monochrome.SFDiamondRighthalfFilled: ImageVector
    get() {
        if (_sFDiamondRighthalfFilled != null) {
            return _sFDiamondRighthalfFilled!!
        }
        _sFDiamondRighthalfFilled = sfIcon(
            name = "Monochrome.SFDiamondRighthalfFilled",
            viewportWidth = 28.6035f,
            viewportHeight = 28.2642f
        ) {
            addSfPath("M26.8945 16.8128C28.6816 15.0257 28.7012 13.2385 26.9238 11.4612L16.7871 1.33425C15-0.452858 13.2227-0.443093 11.4258 1.35378L1.33789 11.4417C-0.449219 13.2288-0.439453 15.0257 1.32812 16.7932L11.4551 26.93C13.2227 28.6975 15.0195 28.6878 16.8066 26.9007ZM14.1309 1.87136L14.1309 26.3831C13.623 26.3831 13.125 26.1487 12.627 25.6409L2.60742 15.6311C1.61133 14.6448 1.64062 13.5999 2.59766 12.6331L12.6367 2.60378C13.125 2.10574 13.623 1.86159 14.1309 1.87136Z", fillAlpha = 0.85f)
        }
        return _sFDiamondRighthalfFilled!!
    }

private var _sFDiamondRighthalfFilled: ImageVector? = null
