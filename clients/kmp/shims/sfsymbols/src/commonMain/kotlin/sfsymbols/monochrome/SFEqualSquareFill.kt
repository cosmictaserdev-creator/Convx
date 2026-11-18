package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFEqualSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFEqualSquareFill: ImageVector
    get() {
        if (_sFEqualSquareFill != null) {
            return _sFEqualSquareFill!!
        }
        _sFEqualSquareFill = sfIcon(
            name = "Monochrome.SFEqualSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM6.49414 12.998C5.9082 12.998 5.52734 13.291 5.52734 13.8574C5.52734 14.4141 5.92773 14.707 6.49414 14.707L16.4551 14.707C17.0312 14.707 17.4316 14.4141 17.4316 13.8574C17.4316 13.291 17.0508 12.998 16.4551 12.998ZM6.49414 8.27148C5.9082 8.27148 5.52734 8.56445 5.52734 9.14062C5.52734 9.69727 5.92773 9.98047 6.49414 9.98047L16.4551 9.98047C17.0312 9.98047 17.4316 9.69727 17.4316 9.14062C17.4316 8.56445 17.0508 8.27148 16.4551 8.27148Z", fillAlpha = 0.85f)
        }
        return _sFEqualSquareFill!!
    }

private var _sFEqualSquareFill: ImageVector? = null
