package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowshapeDown (dualtone)
 * Viewport: 23.1738 x 26.8262
 */
public val SfSymbols.Dualtone.SFArrowshapeDown: ImageVector
    get() {
        if (_sFArrowshapeDown != null) {
            return _sFArrowshapeDown!!
        }
        _sFArrowshapeDown = sfIcon(
            name = "Dualtone.SFArrowshapeDown",
            viewportWidth = 23.1738f,
            viewportHeight = 26.8262f
        ) {
            addSfPath("M11.4062 26.8262C11.8066 26.8262 12.2559 26.6895 12.7441 26.1719L22.0117 16.2402C22.5586 15.6641 22.8125 15.2734 22.8125 14.7168C22.8125 13.916 22.1777 13.3691 21.4062 13.3691L16.4746 13.3691L16.4746 2.96875C16.4746 1.07422 15.4199 0 13.5742 0L9.18945 0C7.34375 0 6.29883 1.07422 6.29883 2.96875L6.29883 13.3691L1.34766 13.3691C0.576172 13.3691 0 13.916 0 14.6973C0 15.2539 0.244141 15.7129 0.732422 16.2402L10.0781 26.1719C10.5566 26.6797 11.0059 26.8262 11.4062 26.8262ZM11.4062 24.9121C11.2988 24.9121 11.2012 24.8633 11.0547 24.707L2.41211 15.5273C2.32422 15.4395 2.28516 15.3613 2.28516 15.2832C2.28516 15.166 2.36328 15.0977 2.49023 15.0977L7.5293 15.0977C7.80273 15.0977 7.92969 14.9805 7.92969 14.6973L7.92969 2.98828C7.92969 2.11914 8.4082 1.63086 9.26758 1.63086L13.4961 1.63086C14.3652 1.63086 14.8438 2.11914 14.8438 2.98828L14.8438 14.6973C14.8438 14.9805 14.9609 15.0977 15.2441 15.0977L20.4102 15.0977C20.5273 15.0977 20.6055 15.1758 20.6055 15.293C20.6055 15.3906 20.5469 15.459 20.4785 15.5371L11.7578 24.707C11.6113 24.8535 11.5137 24.9121 11.4062 24.9121Z", fillAlpha = 0.85f)
        }
        return _sFArrowshapeDown!!
    }

private var _sFArrowshapeDown: ImageVector? = null
