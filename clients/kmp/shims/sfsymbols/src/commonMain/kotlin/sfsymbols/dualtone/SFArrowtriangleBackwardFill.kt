package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowtriangleBackwardFill (dualtone)
 * Viewport: 21.4941 x 19.9707
 */
public val SfSymbols.Dualtone.SFArrowtriangleBackwardFill: ImageVector
    get() {
        if (_sFArrowtriangleBackwardFill != null) {
            return _sFArrowtriangleBackwardFill!!
        }
        _sFArrowtriangleBackwardFill = sfIcon(
            name = "Dualtone.SFArrowtriangleBackwardFill",
            viewportWidth = 21.4941f,
            viewportHeight = 19.9707f
        ) {
            addSfPath("M19.1309 0.0195312C18.6719 0.0195312 18.3203 0.214844 17.832 0.458984L1.25977 8.47656C0.3125 8.94531 0 9.38477 0 9.99023C0 10.6055 0.3125 11.0449 1.25977 11.5039L17.832 19.5312C18.3301 19.7754 18.6816 19.9707 19.1406 19.9707C19.9805 19.9707 20.498 19.3262 20.498 18.3301L20.498 1.66016C20.498 0.654297 19.9707 0.0195312 19.1309 0.0195312Z", fillAlpha = 0.85f)
        }
        return _sFArrowtriangleBackwardFill!!
    }

private var _sFArrowtriangleBackwardFill: ImageVector? = null
