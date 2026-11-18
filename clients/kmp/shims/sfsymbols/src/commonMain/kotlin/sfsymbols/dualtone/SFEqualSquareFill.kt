package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFEqualSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFEqualSquareFill: ImageVector
    get() {
        if (_sFEqualSquareFill != null) {
            return _sFEqualSquareFill!!
        }
        _sFEqualSquareFill = sfIcon(
            name = "Dualtone.SFEqualSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M6.49414 14.707C5.92773 14.707 5.52734 14.4141 5.52734 13.8574C5.52734 13.291 5.9082 12.998 6.49414 12.998L16.4551 12.998C17.0508 12.998 17.4316 13.291 17.4316 13.8574C17.4316 14.4141 17.0312 14.707 16.4551 14.707ZM6.49414 9.98047C5.92773 9.98047 5.52734 9.69727 5.52734 9.14062C5.52734 8.56445 5.9082 8.27148 6.49414 8.27148L16.4551 8.27148C17.0508 8.27148 17.4316 8.56445 17.4316 9.14062C17.4316 9.69727 17.0312 9.98047 16.4551 9.98047Z", fillAlpha = 0.85f)
        }
        return _sFEqualSquareFill!!
    }

private var _sFEqualSquareFill: ImageVector? = null
