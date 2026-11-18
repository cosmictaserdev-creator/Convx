package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowtriangleLeft (monochrome)
 * Viewport: 21.4941 x 19.9707
 */
public val SfSymbols.Monochrome.SFArrowtriangleLeft: ImageVector
    get() {
        if (_sFArrowtriangleLeft != null) {
            return _sFArrowtriangleLeft!!
        }
        _sFArrowtriangleLeft = sfIcon(
            name = "Monochrome.SFArrowtriangleLeft",
            viewportWidth = 21.4941f,
            viewportHeight = 19.9707f
        ) {
            addSfPath("M19.1309 0.0195312C18.6719 0.0195312 18.3203 0.214844 17.832 0.458984L1.25977 8.47656C0.3125 8.94531 0 9.38477 0 9.99023C0 10.6055 0.3125 11.0449 1.25977 11.5039L17.832 19.5312C18.3301 19.7754 18.6816 19.9707 19.1406 19.9707C19.9805 19.9707 20.498 19.3262 20.498 18.3301L20.498 1.66016C20.498 0.654297 19.9707 0.0195312 19.1309 0.0195312ZM18.6035 2.05078C18.7012 2.05078 18.7598 2.11914 18.7598 2.24609L18.7695 17.7344C18.7695 17.8711 18.7109 17.9395 18.623 17.9395C18.5449 17.9395 18.4863 17.9004 18.418 17.8711L2.44141 10.1953C2.36328 10.1562 2.27539 10.0879 2.27539 9.99023C2.27539 9.90234 2.36328 9.83398 2.44141 9.79492L18.4082 2.11914C18.4766 2.08008 18.5352 2.05078 18.6035 2.05078Z", fillAlpha = 0.85f)
        }
        return _sFArrowtriangleLeft!!
    }

private var _sFArrowtriangleLeft: ImageVector? = null
