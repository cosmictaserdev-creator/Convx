package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFButtonHorizontalTopFill (dualtone)
 * Viewport: 30.2734 x 15.8691
 */
public val SfSymbols.Dualtone.SFButtonHorizontalTopFill: ImageVector
    get() {
        if (_sFButtonHorizontalTopFill != null) {
            return _sFButtonHorizontalTopFill!!
        }
        _sFButtonHorizontalTopFill = sfIcon(
            name = "Dualtone.SFButtonHorizontalTopFill",
            viewportWidth = 30.2734f,
            viewportHeight = 15.8691f
        ) {
            addSfPath("M23.5254 11.8066C27.2461 11.8066 29.9121 9.48242 29.9121 5.91797C29.9121 2.35352 27.2461 0.0292969 23.5254 0.0292969L6.38672 0.0292969C2.66602 0.0292969 0 2.35352 0 5.91797C0 9.48242 2.66602 11.8066 6.38672 11.8066ZM23.5254 15.8691C26.9043 15.8691 29.5117 13.877 29.834 10.6348C28.4082 12.3633 26.1621 13.3691 23.5254 13.3691L6.38672 13.3691C3.75 13.3691 1.49414 12.3633 0.078125 10.6445C0.410156 13.877 2.99805 15.8691 6.38672 15.8691Z", fillAlpha = 0.85f)
        }
        return _sFButtonHorizontalTopFill!!
    }

private var _sFButtonHorizontalTopFill: ImageVector? = null
