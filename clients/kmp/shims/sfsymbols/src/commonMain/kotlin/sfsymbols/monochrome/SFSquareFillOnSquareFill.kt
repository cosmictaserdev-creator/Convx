package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSquareFillOnSquareFill (monochrome)
 * Viewport: 29.1211 x 29.0137
 */
public val SfSymbols.Monochrome.SFSquareFillOnSquareFill: ImageVector
    get() {
        if (_sFSquareFillOnSquareFill != null) {
            return _sFSquareFillOnSquareFill!!
        }
        _sFSquareFillOnSquareFill = sfIcon(
            name = "Monochrome.SFSquareFillOnSquareFill",
            viewportWidth = 29.1211f,
            viewportHeight = 29.0137f
        ) {
            addSfPath("M21.4551 5.3418L21.4551 5.64453L11.1133 5.64453C7.72461 5.64453 5.75195 7.59766 5.75195 10.9668L5.75195 21.8457L5 21.8457C2.4707 21.8457 1.19141 20.5762 1.19141 18.0762L1.19141 5.3418C1.19141 2.85156 2.4707 1.58203 5 1.58203L17.6562 1.58203C20.166 1.58203 21.4551 2.86133 21.4551 5.3418Z", fillAlpha = 0.85f)
            addSfPath("M11.1133 27.4609L23.7598 27.4609C26.2793 27.4609 27.5684 26.1816 27.5684 23.6914L27.5684 10.9668C27.5684 8.47656 26.2793 7.19727 23.7598 7.19727L11.1133 7.19727C8.58398 7.19727 7.30469 8.4668 7.30469 10.9668L7.30469 23.6914C7.30469 26.1914 8.58398 27.4609 11.1133 27.4609Z", fillAlpha = 0.85f)
        }
        return _sFSquareFillOnSquareFill!!
    }

private var _sFSquareFillOnSquareFill: ImageVector? = null
