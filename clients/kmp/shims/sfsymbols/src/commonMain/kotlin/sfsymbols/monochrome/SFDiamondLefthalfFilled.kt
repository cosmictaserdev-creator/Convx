package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDiamondLefthalfFilled (monochrome)
 * Viewport: 28.6086 x 28.2642
 */
public val SfSymbols.Monochrome.SFDiamondLefthalfFilled: ImageVector
    get() {
        if (_sFDiamondLefthalfFilled != null) {
            return _sFDiamondLefthalfFilled!!
        }
        _sFDiamondLefthalfFilled = sfIcon(
            name = "Monochrome.SFDiamondLefthalfFilled",
            viewportWidth = 28.6086f,
            viewportHeight = 28.2642f
        ) {
            addSfPath("M1.35995 16.8128L11.4478 26.9007C13.235 28.6878 15.0318 28.6975 16.7994 26.93L26.9264 16.7932C28.6939 15.0257 28.6939 13.2288 26.9068 11.4417L16.8189 1.35378C15.0318-0.443093 13.2447-0.452858 11.4674 1.33425L1.32089 11.4612C-0.456453 13.2385-0.436922 15.0257 1.35995 16.8128ZM14.1236 1.87136C14.6217 1.86159 15.1197 2.10574 15.6178 2.60378L25.6471 12.6331C26.6139 13.5999 26.6432 14.6448 25.6471 15.6311L15.6275 25.6409C15.1197 26.1487 14.6217 26.3831 14.1236 26.3831Z", fillAlpha = 0.85f)
        }
        return _sFDiamondLefthalfFilled!!
    }

private var _sFDiamondLefthalfFilled: ImageVector? = null
