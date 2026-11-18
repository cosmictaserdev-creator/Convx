package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowTriangleheadClockwise (monochrome)
 * Viewport: 25.8008 x 30.311
 */
public val SfSymbols.Monochrome.SFArrowTriangleheadClockwise: ImageVector
    get() {
        if (_sFArrowTriangleheadClockwise != null) {
            return _sFArrowTriangleheadClockwise!!
        }
        _sFArrowTriangleheadClockwise = sfIcon(
            name = "Monochrome.SFArrowTriangleheadClockwise",
            viewportWidth = 25.8008f,
            viewportHeight = 30.311f
        ) {
            addSfPath("M10.3613 0.795162L10.3613 6.48852C10.3613 7.27954 10.9082 7.49438 11.5332 7.05493L15.4883 4.28149C16.0059 3.92016 16.0156 3.38305 15.4883 3.00219L11.543 0.228756C10.9082-0.220463 10.3613 0.00414614 10.3613 0.795162ZM12.7148 27.8655C19.7363 27.8655 25.4395 22.1721 25.4395 15.1506C25.4395 10.7854 23.252 6.98657 19.9121 4.69165C19.4434 4.34008 18.8281 4.46704 18.5645 4.89672C18.3008 5.34594 18.4375 5.85376 18.877 6.18579C21.7285 8.11938 23.6133 11.4104 23.6133 15.1506C23.6133 21.176 18.7402 26.0491 12.7148 26.0491C6.68945 26.0491 1.81641 21.176 1.81641 15.1506C1.81641 9.12524 6.68945 4.25219 12.7246 4.25219C13.2324 4.25219 13.6328 3.83227 13.6328 3.33422C13.6328 2.84594 13.2422 2.43579 12.7344 2.42602C5.68359 2.43579 0 8.12915 0 15.1506C0 22.1721 5.69336 27.8655 12.7148 27.8655Z", fillAlpha = 0.85f)
        }
        return _sFArrowTriangleheadClockwise!!
    }

private var _sFArrowTriangleheadClockwise: ImageVector? = null
