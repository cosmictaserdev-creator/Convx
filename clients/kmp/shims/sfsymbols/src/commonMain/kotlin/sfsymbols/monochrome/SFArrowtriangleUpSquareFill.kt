package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowtriangleUpSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFArrowtriangleUpSquareFill: ImageVector
    get() {
        if (_sFArrowtriangleUpSquareFill != null) {
            return _sFArrowtriangleUpSquareFill!!
        }
        _sFArrowtriangleUpSquareFill = sfIcon(
            name = "Monochrome.SFArrowtriangleUpSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM10.8301 6.83594L6.43555 14.209C6.12305 14.7266 6.37695 15.4199 6.95312 15.4199L16.0156 15.4199C16.5918 15.4199 16.8457 14.7656 16.5234 14.209L12.1582 6.83594C11.8457 6.29883 11.1328 6.32812 10.8301 6.83594Z", fillAlpha = 0.85f)
        }
        return _sFArrowtriangleUpSquareFill!!
    }

private var _sFArrowtriangleUpSquareFill: ImageVector? = null
