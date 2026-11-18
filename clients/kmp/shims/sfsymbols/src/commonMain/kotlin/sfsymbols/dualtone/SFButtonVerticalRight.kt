package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFButtonVerticalRight (dualtone)
 * Viewport: 16.2012 x 29.9121
 */
public val SfSymbols.Dualtone.SFButtonVerticalRight: ImageVector
    get() {
        if (_sFButtonVerticalRight != null) {
            return _sFButtonVerticalRight!!
        }
        _sFButtonVerticalRight = sfIcon(
            name = "Dualtone.SFButtonVerticalRight",
            viewportWidth = 16.2012f,
            viewportHeight = 29.9121f
        ) {
            addSfPath("M4.0625 23.5254C4.0625 27.2461 6.38672 29.9121 9.95117 29.9121C13.5254 29.9121 15.8398 27.2461 15.8398 23.5254L15.8398 6.38672C15.8398 2.66602 13.5254 0 9.95117 0C6.38672 0 4.0625 2.66602 4.0625 6.38672ZM5.79102 23.5254L5.79102 6.38672C5.79102 3.7207 7.43164 1.72852 9.95117 1.72852C12.4609 1.72852 14.1113 3.7207 14.1113 6.38672L14.1113 23.5254C14.1113 26.1914 12.4609 28.1836 9.95117 28.1836C7.43164 28.1836 5.79102 26.1914 5.79102 23.5254ZM0 23.5254C0 27.4023 2.63672 29.9121 6.68945 29.9121L9.95117 29.9121L9.95117 28.1836L6.68945 28.1836C3.70117 28.1836 1.72852 26.3379 1.72852 23.5254L1.72852 6.38672C1.72852 3.57422 3.70117 1.72852 6.68945 1.72852L9.95117 1.72852L9.95117 0L6.68945 0C2.63672 0 0 2.51953 0 6.38672Z", fillAlpha = 0.85f)
        }
        return _sFButtonVerticalRight!!
    }

private var _sFButtonVerticalRight: ImageVector? = null
