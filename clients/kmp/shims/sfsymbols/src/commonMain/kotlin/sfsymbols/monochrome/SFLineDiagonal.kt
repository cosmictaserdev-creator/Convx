package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLineDiagonal (monochrome)
 * Viewport: 19.7144 x 19.375
 */
public val SfSymbols.Monochrome.SFLineDiagonal: ImageVector
    get() {
        if (_sFLineDiagonal != null) {
            return _sFLineDiagonal!!
        }
        _sFLineDiagonal = sfIcon(
            name = "Monochrome.SFLineDiagonal",
            viewportWidth = 19.7144f,
            viewportHeight = 19.375f
        ) {
            addSfPath("M0.252699 17.9053C-0.0890981 18.2373-0.0793324 18.7842 0.252699 19.126C0.58473 19.458 1.14137 19.458 1.4734 19.126L19.1004 1.49902C19.4422 1.15723 19.4324 0.610352 19.1004 0.27832C18.7683-0.0634766 18.2117-0.0634766 17.8797 0.27832Z", fillAlpha = 0.85f)
        }
        return _sFLineDiagonal!!
    }

private var _sFLineDiagonal: ImageVector? = null
