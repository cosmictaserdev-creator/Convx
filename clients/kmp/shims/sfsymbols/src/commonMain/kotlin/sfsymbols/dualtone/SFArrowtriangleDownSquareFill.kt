package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowtriangleDownSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFArrowtriangleDownSquareFill: ImageVector
    get() {
        if (_sFArrowtriangleDownSquareFill != null) {
            return _sFArrowtriangleDownSquareFill!!
        }
        _sFArrowtriangleDownSquareFill = sfIcon(
            name = "Dualtone.SFArrowtriangleDownSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M10.8301 16.2988L6.43555 8.93555C6.12305 8.41797 6.37695 7.72461 6.95312 7.72461L16.0156 7.72461C16.5918 7.72461 16.8457 8.37891 16.5234 8.93555L12.1582 16.2988C11.8457 16.8359 11.1328 16.8066 10.8301 16.2988Z", fillAlpha = 0.85f)
        }
        return _sFArrowtriangleDownSquareFill!!
    }

private var _sFArrowtriangleDownSquareFill: ImageVector? = null
