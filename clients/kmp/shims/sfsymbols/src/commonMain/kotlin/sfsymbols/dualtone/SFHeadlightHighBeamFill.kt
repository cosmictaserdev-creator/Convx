package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFHeadlightHighBeamFill (dualtone)
 * Viewport: 35.4199 x 24.5801
 */
public val SfSymbols.Dualtone.SFHeadlightHighBeamFill: ImageVector
    get() {
        if (_sFHeadlightHighBeamFill != null) {
            return _sFHeadlightHighBeamFill!!
        }
        _sFHeadlightHighBeamFill = sfIcon(
            name = "Dualtone.SFHeadlightHighBeamFill",
            viewportWidth = 35.4199f,
            viewportHeight = 24.5801f
        ) {
            addSfPath("M11.6935 18.269C11.7888 18.7491 11.9056 19.2282 12.041 19.707C12.0674 19.7975 12.0947 19.8866 12.1267 19.9686L2.63672 19.9609C2.17773 19.9609 1.77734 19.5801 1.77734 19.1211C1.77734 18.6621 2.17773 18.2617 2.63672 18.2617ZM11.2743 15.4102L2.63672 15.4102C2.17773 15.4102 1.77734 15.0293 1.77734 14.5605C1.77734 14.1016 2.17773 13.7012 2.63672 13.7012L11.1694 13.7012C11.1865 14.2733 11.2202 14.8427 11.2743 15.4102ZM11.1701 10.8467L2.63672 10.8398C2.1875 10.8398 1.77734 10.459 1.77734 9.99023C1.77734 9.53125 2.17773 9.14062 2.63672 9.14062L11.2756 9.14063C11.2213 9.70691 11.1874 10.2752 11.1701 10.8467ZM12.041 4.84375C11.9066 5.3228 11.7905 5.80111 11.6958 6.2793L2.63672 6.2793C2.17773 6.2793 1.77734 5.91797 1.77734 5.42969C1.77734 4.9707 2.17773 4.58008 2.63672 4.58008L12.1284 4.58008Z", fillAlpha = 0.425f)
            addSfPath("M13.2715 12.2852C13.2715 14.6777 13.457 16.8848 14.0918 19.1211C14.7949 21.5332 16.2305 22.4219 19.0625 22.4219L19.707 22.4219C26.7969 22.4219 33.2812 17.7539 33.2812 12.2852C33.2812 6.81641 26.7969 2.13867 19.707 2.13867L19.0625 2.13867C16.2402 2.13867 14.8047 3.02734 14.0918 5.43945C13.457 7.67578 13.2715 9.89258 13.2715 12.2852Z", fillAlpha = 0.85f)
        }
        return _sFHeadlightHighBeamFill!!
    }

private var _sFHeadlightHighBeamFill: ImageVector? = null
