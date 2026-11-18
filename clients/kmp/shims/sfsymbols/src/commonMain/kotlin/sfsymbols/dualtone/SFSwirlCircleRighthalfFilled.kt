package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSwirlCircleRighthalfFilled (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFSwirlCircleRighthalfFilled: ImageVector
    get() {
        if (_sFSwirlCircleRighthalfFilled != null) {
            return _sFSwirlCircleRighthalfFilled!!
        }
        _sFSwirlCircleRighthalfFilled = sfIcon(
            name = "Dualtone.SFSwirlCircleRighthalfFilled",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 1.75781C9.84375 1.75781 7.24609 4.10156 7.24609 7.23633C7.24609 9.67773 9.35547 11.7871 12.7148 12.7246C16.084 13.6719 18.2129 15.8008 18.2129 18.2031C18.2129 21.2207 15.7715 23.6816 12.7148 23.6816C18.7793 23.6816 23.6816 18.7793 23.6816 12.7246C23.6816 6.66016 18.7793 1.75781 12.7148 1.75781ZM12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
        }
        return _sFSwirlCircleRighthalfFilled!!
    }

private var _sFSwirlCircleRighthalfFilled: ImageVector? = null
