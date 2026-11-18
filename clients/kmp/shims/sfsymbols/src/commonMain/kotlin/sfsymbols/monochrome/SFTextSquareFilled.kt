package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTextSquareFilled (monochrome)
 * Viewport: 25.4785 x 24.7363
 */
public val SfSymbols.Monochrome.SFTextSquareFilled: ImageVector
    get() {
        if (_sFTextSquareFilled != null) {
            return _sFTextSquareFilled!!
        }
        _sFTextSquareFilled = sfIcon(
            name = "Monochrome.SFTextSquareFilled",
            viewportWidth = 25.4785f,
            viewportHeight = 24.7363f
        ) {
            addSfPath("M0.869141 23.1152L24.2383 23.1152C24.7266 23.1152 25.1172 22.7441 25.1172 22.2559C25.1172 21.7578 24.7266 21.3867 24.2383 21.3867L0.869141 21.3867C0.371094 21.3867 0 21.7578 0 22.2559C0 22.7441 0.371094 23.1152 0.869141 23.1152Z", fillAlpha = 0.85f)
            addSfPath("M0.869141 16.5234L24.2383 16.5234C24.7266 16.5234 25.1172 16.1426 25.1172 15.6543C25.1172 15.166 24.7266 14.7949 24.2383 14.7949L0.869141 14.7949C0.371094 14.7949 0 15.166 0 15.6543C0 16.1426 0.371094 16.5234 0.869141 16.5234Z", fillAlpha = 0.85f)
            addSfPath("M15.6348 9.93164L24.2383 9.93164C24.7266 9.93164 25.1172 9.55078 25.1172 9.0625C25.1172 8.57422 24.7266 8.19336 24.2383 8.19336L15.6348 8.19336C15.1465 8.19336 14.7754 8.57422 14.7754 9.0625C14.7754 9.55078 15.1465 9.93164 15.6348 9.93164Z", fillAlpha = 0.85f)
            addSfPath("M15.6348 3.33008L24.2383 3.33008C24.7266 3.33008 25.1172 2.95898 25.1172 2.4707C25.1172 1.98242 24.7266 1.60156 24.2383 1.60156L15.6348 1.60156C15.1465 1.60156 14.7754 1.98242 14.7754 2.4707C14.7754 2.95898 15.1465 3.33008 15.6348 3.33008Z", fillAlpha = 0.85f)
            addSfPath("M2.90039 10.8594L9.76562 10.8594C11.0254 10.8594 11.7676 10.1367 11.7676 8.89648L11.7676 1.96289C11.7676 0.732422 11.0254 0 9.76562 0L2.90039 0C1.64062 0 0.898438 0.732422 0.898438 1.96289L0.898438 8.89648C0.898438 10.1367 1.64062 10.8594 2.90039 10.8594Z", fillAlpha = 0.85f)
        }
        return _sFTextSquareFilled!!
    }

private var _sFTextSquareFilled: ImageVector? = null
