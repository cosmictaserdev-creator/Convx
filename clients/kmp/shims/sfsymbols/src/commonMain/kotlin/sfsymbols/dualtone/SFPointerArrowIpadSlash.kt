package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPointerArrowIpadSlash (dualtone)
 * Viewport: 28.9966 x 25.044
 */
public val SfSymbols.Dualtone.SFPointerArrowIpadSlash: ImageVector
    get() {
        if (_sFPointerArrowIpadSlash != null) {
            return _sFPointerArrowIpadSlash!!
        }
        _sFPointerArrowIpadSlash = sfIcon(
            name = "Dualtone.SFPointerArrowIpadSlash",
            viewportWidth = 28.9966f,
            viewportHeight = 25.044f
        ) {
            addSfPath("M13.5033 17.6669L8.9563 22.2046C7.87232 23.2886 6.44654 22.732 6.44654 21.4038L6.44654 10.6276ZM9.04419 2.38039L21.7395 15.0757C22.6575 16.0132 22.0618 17.4195 20.5286 17.4195L19.4661 17.4195L6.44654 4.4258L6.44654 3.38625C6.44654 1.90188 8.02857 1.36477 9.04419 2.38039Z", fillAlpha = 0.425f)
            addSfPath("M1.60279 3.87453L21.2122 23.4351C21.5442 23.7573 22.0715 23.7573 22.3938 23.4351C22.7161 23.103 22.7161 22.5855 22.3938 22.2534L2.79419 2.69289C2.47193 2.38039 1.94459 2.36086 1.60279 2.69289C1.29029 3.01516 1.29029 3.56203 1.60279 3.87453Z", fillAlpha = 0.85f)
        }
        return _sFPointerArrowIpadSlash!!
    }

private var _sFPointerArrowIpadSlash: ImageVector? = null
