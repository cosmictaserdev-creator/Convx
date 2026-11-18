package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDiamondBottomhalfFilled (monochrome)
 * Viewport: 28.6086 x 28.2716
 */
public val SfSymbols.Monochrome.SFDiamondBottomhalfFilled: ImageVector
    get() {
        if (_sFDiamondBottomhalfFilled != null) {
            return _sFDiamondBottomhalfFilled!!
        }
        _sFDiamondBottomhalfFilled = sfIcon(
            name = "Monochrome.SFDiamondBottomhalfFilled",
            viewportWidth = 28.6086f,
            viewportHeight = 28.2716f
        ) {
            addSfPath("M1.35995 11.4454C-0.436922 13.2325-0.456453 15.0098 1.32089 16.7969L11.4674 26.9239C13.2447 28.7012 15.0318 28.6914 16.8189 26.8946L26.9068 16.8164C28.6939 15.0293 28.6939 13.2325 26.9264 11.4649L16.7994 1.3184C15.0318-0.449179 13.235-0.439413 11.4478 1.3477ZM26.3795 14.1309L1.86777 14.1309C1.86777 13.6329 2.10214 13.125 2.60019 12.627L12.6393 2.58793C13.5865 1.6309 14.6314 1.61137 15.6275 2.60746L25.6471 12.6172C26.1353 13.1153 26.3795 13.6329 26.3795 14.1309Z", fillAlpha = 0.85f)
        }
        return _sFDiamondBottomhalfFilled!!
    }

private var _sFDiamondBottomhalfFilled: ImageVector? = null
