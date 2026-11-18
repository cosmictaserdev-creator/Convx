package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFButtonVerticalRightFill (monochrome)
 * Viewport: 16.2012 x 29.9121
 */
public val SfSymbols.Monochrome.SFButtonVerticalRightFill: ImageVector
    get() {
        if (_sFButtonVerticalRightFill != null) {
            return _sFButtonVerticalRightFill!!
        }
        _sFButtonVerticalRightFill = sfIcon(
            name = "Monochrome.SFButtonVerticalRightFill",
            viewportWidth = 16.2012f,
            viewportHeight = 29.9121f
        ) {
            addSfPath("M4.0625 23.5254C4.0625 27.2461 6.38672 29.9121 9.95117 29.9121C13.5254 29.9121 15.8398 27.2461 15.8398 23.5254L15.8398 6.38672C15.8398 2.66602 13.5254 0 9.95117 0C6.38672 0 4.0625 2.66602 4.0625 6.38672ZM0 23.5254C0 26.9043 1.99219 29.5215 5.23438 29.834C3.50586 28.4082 2.5 26.1621 2.5 23.5254L2.5 6.38672C2.5 3.75 3.50586 1.50391 5.22461 0.078125C1.99219 0.410156 0 2.99805 0 6.38672Z", fillAlpha = 0.85f)
        }
        return _sFButtonVerticalRightFill!!
    }

private var _sFButtonVerticalRightFill: ImageVector? = null
