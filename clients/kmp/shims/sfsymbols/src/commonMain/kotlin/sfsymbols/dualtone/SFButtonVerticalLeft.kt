package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFButtonVerticalLeft (dualtone)
 * Viewport: 16.2012 x 29.9121
 */
public val SfSymbols.Dualtone.SFButtonVerticalLeft: ImageVector
    get() {
        if (_sFButtonVerticalLeft != null) {
            return _sFButtonVerticalLeft!!
        }
        _sFButtonVerticalLeft = sfIcon(
            name = "Dualtone.SFButtonVerticalLeft",
            viewportWidth = 16.2012f,
            viewportHeight = 29.9121f
        ) {
            addSfPath("M11.7773 23.5254L11.7773 6.38672C11.7773 2.66602 9.46289 0 5.89844 0C2.32422 0 0 2.66602 0 6.38672L0 23.5254C0 27.2461 2.32422 29.9121 5.89844 29.9121C9.46289 29.9121 11.7773 27.2461 11.7773 23.5254ZM10.0488 23.5254C10.0488 26.1914 8.4082 28.1836 5.89844 28.1836C3.37891 28.1836 1.72852 26.1914 1.72852 23.5254L1.72852 6.38672C1.72852 3.7207 3.37891 1.72852 5.89844 1.72852C8.4082 1.72852 10.0488 3.7207 10.0488 6.38672ZM15.8398 23.5254L15.8398 6.38672C15.8398 2.51953 13.2031 0 9.15039 0L5.89844 0L5.89844 1.72852L9.15039 1.72852C12.1484 1.72852 14.1113 3.57422 14.1113 6.38672L14.1113 23.5254C14.1113 26.3379 12.1484 28.1836 9.15039 28.1836L5.89844 28.1836L5.89844 29.9121L9.15039 29.9121C13.2031 29.9121 15.8398 27.4023 15.8398 23.5254Z", fillAlpha = 0.85f)
        }
        return _sFButtonVerticalLeft!!
    }

private var _sFButtonVerticalLeft: ImageVector? = null
