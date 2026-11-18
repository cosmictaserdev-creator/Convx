package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLineDiagonalTriangleheadUpRight (monochrome)
 * Viewport: 20.0366 x 19.9935
 */
public val SfSymbols.Monochrome.SFLineDiagonalTriangleheadUpRight: ImageVector
    get() {
        if (_sFLineDiagonalTriangleheadUpRight != null) {
            return _sFLineDiagonalTriangleheadUpRight!!
        }
        _sFLineDiagonalTriangleheadUpRight = sfIcon(
            name = "Monochrome.SFLineDiagonalTriangleheadUpRight",
            viewportWidth = 20.0366f,
            viewportHeight = 19.9935f
        ) {
            addSfPath("M10.3015 3.32193L16.3367 9.37662C16.7566 9.79654 17.3035 9.56217 17.4695 8.9567L19.6472 0.890292C19.7937 0.333651 19.3152-0.125333 18.7781 0.030917L10.7117 2.20865C10.116 2.3649 9.88161 2.90201 10.3015 3.32193ZM1.4734 19.4059L16.9714 3.93717C17.3132 3.59537 17.3035 3.0485 16.9714 2.71646C16.6394 2.37467 16.0925 2.37467 15.7507 2.71646L0.252699 18.1852C-0.0890981 18.5172-0.0793324 19.0739 0.252699 19.4059C0.58473 19.7379 1.13161 19.7379 1.4734 19.4059Z", fillAlpha = 0.85f)
        }
        return _sFLineDiagonalTriangleheadUpRight!!
    }

private var _sFLineDiagonalTriangleheadUpRight: ImageVector? = null
