package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowshapeUp (monochrome)
 * Viewport: 23.1738 x 26.8262
 */
public val SfSymbols.Monochrome.SFArrowshapeUp: ImageVector
    get() {
        if (_sFArrowshapeUp != null) {
            return _sFArrowshapeUp!!
        }
        _sFArrowshapeUp = sfIcon(
            name = "Monochrome.SFArrowshapeUp",
            viewportWidth = 23.1738f,
            viewportHeight = 26.8262f
        ) {
            addSfPath("M11.4062 0C11.0059 0 10.5566 0.146484 10.0781 0.664062L0.732422 10.5859C0.244141 11.1133 0 11.5723 0 12.1387C0 12.9199 0.576172 13.457 1.34766 13.457L6.29883 13.457L6.29883 23.8672C6.29883 25.752 7.34375 26.8262 9.18945 26.8262L13.5742 26.8262C15.4199 26.8262 16.4746 25.752 16.4746 23.8672L16.4746 13.457L21.4062 13.457C22.1777 13.457 22.8125 12.9199 22.8125 12.1191C22.8125 11.5527 22.5586 11.1621 22.0117 10.5859L12.7441 0.664062C12.2559 0.136719 11.8066 0 11.4062 0ZM11.4062 1.92383C11.5137 1.92383 11.6113 1.97266 11.7578 2.11914L20.4785 11.2891C20.5469 11.3672 20.6055 11.4453 20.6055 11.543C20.6055 11.6504 20.5273 11.7285 20.4102 11.7285L15.2441 11.7285C14.9609 11.7285 14.8438 11.8555 14.8438 12.1289L14.8438 23.8477C14.8438 24.7168 14.3652 25.1953 13.4961 25.1953L9.26758 25.1953C8.4082 25.1953 7.92969 24.7168 7.92969 23.8477L7.92969 12.1289C7.92969 11.8555 7.80273 11.7285 7.5293 11.7285L2.49023 11.7285C2.36328 11.7285 2.28516 11.6602 2.28516 11.543C2.28516 11.4648 2.32422 11.3867 2.41211 11.2988L11.0547 2.11914C11.2012 1.96289 11.2988 1.92383 11.4062 1.92383Z", fillAlpha = 0.85f)
        }
        return _sFArrowshapeUp!!
    }

private var _sFArrowshapeUp: ImageVector? = null
