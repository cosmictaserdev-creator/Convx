package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCircleGrid2x1RightFilled (dualtone)
 * Viewport: 32.6758 x 14.4043
 */
public val SfSymbols.Dualtone.SFCircleGrid2x1RightFilled: ImageVector
    get() {
        if (_sFCircleGrid2x1RightFilled != null) {
            return _sFCircleGrid2x1RightFilled!!
        }
        _sFCircleGrid2x1RightFilled = sfIcon(
            name = "Dualtone.SFCircleGrid2x1RightFilled",
            viewportWidth = 32.6758f,
            viewportHeight = 14.4043f
        ) {
            addSfPath("M7.1875 14.3848C11.1621 14.3848 14.3848 11.1621 14.3848 7.19727C14.3848 3.22266 11.1621 0 7.1875 0C3.22266 0 0 3.22266 0 7.19727C0 11.1621 3.22266 14.3848 7.1875 14.3848ZM7.1875 12.6562C4.16992 12.6562 1.72852 10.2148 1.72852 7.19727C1.72852 4.16992 4.16992 1.72852 7.1875 1.72852C10.2148 1.72852 12.6562 4.16992 12.6562 7.19727C12.6562 10.2148 10.2148 12.6562 7.1875 12.6562Z", fillAlpha = 0.425f)
            addSfPath("M25.127 14.3848C29.1016 14.3848 32.3145 11.1621 32.3145 7.19727C32.3145 3.22266 29.1016 0 25.127 0C21.1523 0 17.9395 3.22266 17.9395 7.19727C17.9395 11.1621 21.1523 14.3848 25.127 14.3848Z", fillAlpha = 0.85f)
        }
        return _sFCircleGrid2x1RightFilled!!
    }

private var _sFCircleGrid2x1RightFilled: ImageVector? = null
