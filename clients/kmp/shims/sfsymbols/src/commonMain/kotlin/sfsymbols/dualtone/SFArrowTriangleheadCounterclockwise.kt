package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowTriangleheadCounterclockwise (dualtone)
 * Viewport: 25.8008 x 30.311
 */
public val SfSymbols.Dualtone.SFArrowTriangleheadCounterclockwise: ImageVector
    get() {
        if (_sFArrowTriangleheadCounterclockwise != null) {
            return _sFArrowTriangleheadCounterclockwise!!
        }
        _sFArrowTriangleheadCounterclockwise = sfIcon(
            name = "Dualtone.SFArrowTriangleheadCounterclockwise",
            viewportWidth = 25.8008f,
            viewportHeight = 30.311f
        ) {
            addSfPath("M15.0781 0.795162C15.0781 0.00414614 14.5312-0.220463 13.8965 0.228756L9.95117 3.00219C9.41406 3.38305 9.42383 3.92016 9.95117 4.28149L13.9062 7.05493C14.5312 7.49438 15.0781 7.27954 15.0781 6.48852ZM12.7148 27.8655C19.7363 27.8655 25.4395 22.1721 25.4395 15.1506C25.4395 8.12915 19.7461 2.43579 12.7051 2.42602C12.1973 2.43579 11.8066 2.84594 11.8066 3.33422C11.8066 3.83227 12.207 4.25219 12.7148 4.25219C18.7402 4.25219 23.6133 9.12524 23.6133 15.1506C23.6133 21.176 18.7402 26.0491 12.7148 26.0491C6.68945 26.0491 1.81641 21.176 1.81641 15.1506C1.81641 11.4104 3.71094 8.11938 6.5625 6.18579C7.00195 5.85376 7.13867 5.34594 6.86523 4.89672C6.61133 4.46704 5.99609 4.34008 5.51758 4.69165C2.1875 6.98657 0 10.7854 0 15.1506C0 22.1721 5.69336 27.8655 12.7148 27.8655Z", fillAlpha = 0.85f)
        }
        return _sFArrowTriangleheadCounterclockwise!!
    }

private var _sFArrowTriangleheadCounterclockwise: ImageVector? = null
