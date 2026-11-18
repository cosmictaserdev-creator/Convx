package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowtriangleUpSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFArrowtriangleUpSquareFill: ImageVector
    get() {
        if (_sFArrowtriangleUpSquareFill != null) {
            return _sFArrowtriangleUpSquareFill!!
        }
        _sFArrowtriangleUpSquareFill = sfIcon(
            name = "Dualtone.SFArrowtriangleUpSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M6.95312 15.4199C6.37695 15.4199 6.12305 14.7266 6.43555 14.209L10.8301 6.83594C11.1328 6.32812 11.8457 6.29883 12.1582 6.83594L16.5234 14.209C16.8457 14.7656 16.5918 15.4199 16.0156 15.4199Z", fillAlpha = 0.85f)
        }
        return _sFArrowtriangleUpSquareFill!!
    }

private var _sFArrowtriangleUpSquareFill: ImageVector? = null
