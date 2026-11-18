package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowshapeDownFill (dualtone)
 * Viewport: 23.1738 x 26.8652
 */
public val SfSymbols.Dualtone.SFArrowshapeDownFill: ImageVector
    get() {
        if (_sFArrowshapeDownFill != null) {
            return _sFArrowshapeDownFill!!
        }
        _sFArrowshapeDownFill = sfIcon(
            name = "Dualtone.SFArrowshapeDownFill",
            viewportWidth = 23.1738f,
            viewportHeight = 26.8652f
        ) {
            addSfPath("M16.2109 2.76367C16.2109 1.00586 15.2441 0 13.5156 0L9.24805 0C7.5293 0 6.55273 1.00586 6.55273 2.76367L6.55273 18.2812C6.55273 20.0391 7.5293 21.0449 9.24805 21.0449L13.5156 21.0449C15.2441 21.0449 16.2109 20.0391 16.2109 18.2812ZM1.34766 13.3691C0.576172 13.3691 0 13.916 0 14.6973C0 15.2539 0.244141 15.7129 0.732422 16.2402L10.0781 26.1719C10.5566 26.6797 11.0059 26.8262 11.4062 26.8262C11.8066 26.8262 12.2559 26.6895 12.7441 26.1719L22.0117 16.2402C22.5586 15.6641 22.8125 15.2734 22.8125 14.7168C22.8125 13.916 22.1777 13.3691 21.4062 13.3691Z", fillAlpha = 0.85f)
        }
        return _sFArrowshapeDownFill!!
    }

private var _sFArrowshapeDownFill: ImageVector? = null
