package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTriangleTophalfFilled (monochrome)
 * Viewport: 26.543 x 23.8379
 */
public val SfSymbols.Monochrome.SFTriangleTophalfFilled: ImageVector
    get() {
        if (_sFTriangleTophalfFilled != null) {
            return _sFTriangleTophalfFilled!!
        }
        _sFTriangleTophalfFilled = sfIcon(
            name = "Monochrome.SFTriangleTophalfFilled",
            viewportWidth = 26.543f,
            viewportHeight = 23.8379f
        ) {
            addSfPath("M0 20.4004C0 22.2559 1.30859 23.75 3.34961 23.75L22.8418 23.75C24.873 23.75 26.1816 22.2559 26.1816 20.4004C26.1816 19.8535 26.0352 19.2773 25.7324 18.75L15.9668 1.68945C15.3418 0.576172 14.2285 0 13.0957 0C11.9629 0 10.8398 0.576172 10.2148 1.68945L0.439453 18.7695C0.146484 19.2871 0 19.8535 0 20.4004ZM1.74805 20.4004C1.74805 20.1562 1.79688 19.8633 1.93359 19.5703L5.6543 13.0664L20.498 13.0664L24.2188 19.5801C24.375 19.8535 24.4336 20.1465 24.4336 20.4004C24.4336 21.2695 23.7695 22.0605 22.8125 22.0605L3.35938 22.0605C2.39258 22.0605 1.74805 21.2695 1.74805 20.4004Z", fillAlpha = 0.85f)
        }
        return _sFTriangleTophalfFilled!!
    }

private var _sFTriangleTophalfFilled: ImageVector? = null
