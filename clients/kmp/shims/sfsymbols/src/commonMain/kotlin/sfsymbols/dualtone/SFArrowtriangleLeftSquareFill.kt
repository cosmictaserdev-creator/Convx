package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowtriangleLeftSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFArrowtriangleLeftSquareFill: ImageVector
    get() {
        if (_sFArrowtriangleLeftSquareFill != null) {
            return _sFArrowtriangleLeftSquareFill!!
        }
        _sFArrowtriangleLeftSquareFill = sfIcon(
            name = "Dualtone.SFArrowtriangleLeftSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M6.34766 12.1484C5.83008 11.8359 5.83984 11.123 6.34766 10.8203L13.7207 6.43555C14.2383 6.11328 14.9316 6.37695 14.9316 6.95312L14.9316 16.0059C14.9316 16.582 14.2773 16.8555 13.7207 16.5137Z", fillAlpha = 0.85f)
        }
        return _sFArrowtriangleLeftSquareFill!!
    }

private var _sFArrowtriangleLeftSquareFill: ImageVector? = null
