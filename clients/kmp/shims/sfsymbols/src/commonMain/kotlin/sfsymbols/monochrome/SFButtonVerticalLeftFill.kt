package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFButtonVerticalLeftFill (monochrome)
 * Viewport: 16.2012 x 29.9121
 */
public val SfSymbols.Monochrome.SFButtonVerticalLeftFill: ImageVector
    get() {
        if (_sFButtonVerticalLeftFill != null) {
            return _sFButtonVerticalLeftFill!!
        }
        _sFButtonVerticalLeftFill = sfIcon(
            name = "Monochrome.SFButtonVerticalLeftFill",
            viewportWidth = 16.2012f,
            viewportHeight = 29.9121f
        ) {
            addSfPath("M11.7773 23.5254L11.7773 6.38672C11.7773 2.66602 9.46289 0 5.89844 0C2.32422 0 0 2.66602 0 6.38672L0 23.5254C0 27.2461 2.32422 29.9121 5.89844 29.9121C9.46289 29.9121 11.7773 27.2461 11.7773 23.5254ZM15.8398 23.5254L15.8398 6.38672C15.8398 2.99805 13.8477 0.410156 10.625 0.078125C12.334 1.50391 13.3398 3.75 13.3398 6.38672L13.3398 23.5254C13.3398 26.1621 12.334 28.4082 10.6152 29.834C13.8477 29.5215 15.8398 26.9043 15.8398 23.5254Z", fillAlpha = 0.85f)
        }
        return _sFButtonVerticalLeftFill!!
    }

private var _sFButtonVerticalLeftFill: ImageVector? = null
