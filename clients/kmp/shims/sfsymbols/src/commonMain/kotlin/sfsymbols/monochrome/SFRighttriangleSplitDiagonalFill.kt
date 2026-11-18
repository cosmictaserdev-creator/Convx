package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRighttriangleSplitDiagonalFill (monochrome)
 * Viewport: 24.2578 x 25.6543
 */
public val SfSymbols.Monochrome.SFRighttriangleSplitDiagonalFill: ImageVector
    get() {
        if (_sFRighttriangleSplitDiagonalFill != null) {
            return _sFRighttriangleSplitDiagonalFill!!
        }
        _sFRighttriangleSplitDiagonalFill = sfIcon(
            name = "Monochrome.SFRighttriangleSplitDiagonalFill",
            viewportWidth = 24.2578f,
            viewportHeight = 25.6543f
        ) {
            addSfPath("M1.5918 23.2715L21.2012 23.2617C21.4551 23.2617 21.6992 23.2227 21.875 23.1348L9.84375 11.1133L0.712891 20.2441C0.263672 20.6836 0 21.1133 0 21.709C0 22.6465 0.605469 23.2715 1.5918 23.2715ZM23.1348 21.9434C23.2227 21.748 23.2617 21.4746 23.2617 21.1914L23.2617 1.5918C23.2617 0.615234 22.6367 0 21.6992 0C21.1035 0 20.6836 0.273438 20.2344 0.722656L11.0742 9.88281Z", fillAlpha = 0.85f)
        }
        return _sFRighttriangleSplitDiagonalFill!!
    }

private var _sFRighttriangleSplitDiagonalFill: ImageVector? = null
