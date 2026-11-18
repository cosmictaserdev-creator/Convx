package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMinusSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFMinusSquareFill: ImageVector
    get() {
        if (_sFMinusSquareFill != null) {
            return _sFMinusSquareFill!!
        }
        _sFMinusSquareFill = sfIcon(
            name = "Monochrome.SFMinusSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM6.21094 10.5566C5.57617 10.5566 5.15625 10.9082 5.15625 11.5039C5.15625 12.0801 5.5957 12.4219 6.21094 12.4219L16.7676 12.4219C17.3828 12.4219 17.8125 12.0801 17.8125 11.5039C17.8125 10.9082 17.4121 10.5566 16.7676 10.5566Z", fillAlpha = 0.85f)
        }
        return _sFMinusSquareFill!!
    }

private var _sFMinusSquareFill: ImageVector? = null
