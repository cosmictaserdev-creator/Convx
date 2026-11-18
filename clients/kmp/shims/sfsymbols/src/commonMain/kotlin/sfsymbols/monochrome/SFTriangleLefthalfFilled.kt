package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTriangleLefthalfFilled (monochrome)
 * Viewport: 26.543 x 23.8379
 */
public val SfSymbols.Monochrome.SFTriangleLefthalfFilled: ImageVector
    get() {
        if (_sFTriangleLefthalfFilled != null) {
            return _sFTriangleLefthalfFilled!!
        }
        _sFTriangleLefthalfFilled = sfIcon(
            name = "Monochrome.SFTriangleLefthalfFilled",
            viewportWidth = 26.543f,
            viewportHeight = 23.8379f
        ) {
            addSfPath("M0 20.4004C0 22.2559 1.29883 23.75 3.33984 23.75L22.832 23.75C24.873 23.75 26.1816 22.2559 26.1816 20.4004C26.1816 19.8535 26.0352 19.2871 25.7324 18.7695L15.9668 1.68945C15.3418 0.576172 14.209 0 13.0859 0C11.9531 0 10.8398 0.576172 10.2148 1.68945L0.449219 18.75C0.146484 19.2773 0 19.8535 0 20.4004ZM13.0859 22.0605L13.0859 1.71875C13.6328 1.71875 14.1895 1.97266 14.4922 2.50977L24.2383 19.5703C24.3848 19.8633 24.4336 20.1562 24.4336 20.4004C24.4336 21.2695 23.7793 22.0605 22.8125 22.0605Z", fillAlpha = 0.85f)
        }
        return _sFTriangleLefthalfFilled!!
    }

private var _sFTriangleLefthalfFilled: ImageVector? = null
