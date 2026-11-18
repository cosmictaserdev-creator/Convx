package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSquareSplit1x2Fill (dualtone)
 * Viewport: 23.3301 x 22.9785
 */
public val SfSymbols.Dualtone.SFSquareSplit1x2Fill: ImageVector
    get() {
        if (_sFSquareSplit1x2Fill != null) {
            return _sFSquareSplit1x2Fill!!
        }
        _sFSquareSplit1x2Fill = sfIcon(
            name = "Dualtone.SFSquareSplit1x2Fill",
            viewportWidth = 23.3301f,
            viewportHeight = 22.9785f
        ) {
            addSfPath("M22.959 10.6348L22.959 12.3633L0 12.3633L0 10.6348ZM0.00976562 3.82812L0.00976562 19.1797C0.00976562 21.6992 1.28906 22.9785 3.7793 22.9785L19.209 22.9785C21.6895 22.9785 22.9688 21.6992 22.9688 19.1797L22.9688 3.82812C22.9688 1.30859 21.709 0.0195312 19.209 0.0195312L3.7793 0.0195312C1.2793 0.0195312 0.00976562 1.30859 0.00976562 3.82812Z", fillAlpha = 0.85f)
        }
        return _sFSquareSplit1x2Fill!!
    }

private var _sFSquareSplit1x2Fill: ImageVector? = null
