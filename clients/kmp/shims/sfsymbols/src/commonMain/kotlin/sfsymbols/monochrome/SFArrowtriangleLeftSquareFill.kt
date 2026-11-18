package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowtriangleLeftSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFArrowtriangleLeftSquareFill: ImageVector
    get() {
        if (_sFArrowtriangleLeftSquareFill != null) {
            return _sFArrowtriangleLeftSquareFill!!
        }
        _sFArrowtriangleLeftSquareFill = sfIcon(
            name = "Monochrome.SFArrowtriangleLeftSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM13.7207 6.43555L6.34766 10.8203C5.83984 11.123 5.83008 11.8359 6.34766 12.1484L13.7207 16.5137C14.2773 16.8555 14.9316 16.582 14.9316 16.0059L14.9316 6.95312C14.9316 6.37695 14.2383 6.11328 13.7207 6.43555Z", fillAlpha = 0.85f)
        }
        return _sFArrowtriangleLeftSquareFill!!
    }

private var _sFArrowtriangleLeftSquareFill: ImageVector? = null
