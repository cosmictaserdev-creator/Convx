package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowtriangleDownSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFArrowtriangleDownSquareFill: ImageVector
    get() {
        if (_sFArrowtriangleDownSquareFill != null) {
            return _sFArrowtriangleDownSquareFill!!
        }
        _sFArrowtriangleDownSquareFill = sfIcon(
            name = "Monochrome.SFArrowtriangleDownSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM6.95312 7.72461C6.37695 7.72461 6.12305 8.41797 6.43555 8.93555L10.8301 16.2988C11.1328 16.8066 11.8457 16.8359 12.1582 16.2988L16.5234 8.93555C16.8457 8.37891 16.5918 7.72461 16.0156 7.72461Z", fillAlpha = 0.85f)
        }
        return _sFArrowtriangleDownSquareFill!!
    }

private var _sFArrowtriangleDownSquareFill: ImageVector? = null
