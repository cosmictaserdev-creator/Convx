package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDiamondTophalfFilled (monochrome)
 * Viewport: 28.6086 x 28.2642
 */
public val SfSymbols.Monochrome.SFDiamondTophalfFilled: ImageVector
    get() {
        if (_sFDiamondTophalfFilled != null) {
            return _sFDiamondTophalfFilled!!
        }
        _sFDiamondTophalfFilled = sfIcon(
            name = "Monochrome.SFDiamondTophalfFilled",
            viewportWidth = 28.6086f,
            viewportHeight = 28.2642f
        ) {
            addSfPath("M1.35995 16.8128L11.4478 26.9007C13.235 28.6878 15.0318 28.6975 16.7994 26.93L26.9264 16.7932C28.6939 15.0257 28.6939 13.2288 26.9068 11.4417L16.8189 1.35378C15.0318-0.443093 13.2447-0.452858 11.4674 1.33425L1.32089 11.4612C-0.456453 13.2385-0.436922 15.0257 1.35995 16.8128ZM26.3795 14.1272C26.3795 14.6253 26.1353 15.1331 25.6471 15.6311L15.6275 25.6409C14.6314 26.637 13.5865 26.6175 12.6393 25.6604L2.60019 15.6214C2.10214 15.1233 1.86777 14.6253 1.86777 14.1272Z", fillAlpha = 0.85f)
        }
        return _sFDiamondTophalfFilled!!
    }

private var _sFDiamondTophalfFilled: ImageVector? = null
