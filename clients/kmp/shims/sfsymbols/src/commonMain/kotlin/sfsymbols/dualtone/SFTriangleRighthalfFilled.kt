package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTriangleRighthalfFilled (dualtone)
 * Viewport: 26.543 x 23.8379
 */
public val SfSymbols.Dualtone.SFTriangleRighthalfFilled: ImageVector
    get() {
        if (_sFTriangleRighthalfFilled != null) {
            return _sFTriangleRighthalfFilled!!
        }
        _sFTriangleRighthalfFilled = sfIcon(
            name = "Dualtone.SFTriangleRighthalfFilled",
            viewportWidth = 26.543f,
            viewportHeight = 23.8379f
        ) {
            addSfPath("M0 20.4004C0 22.2559 1.29883 23.75 3.33984 23.75L22.832 23.75C24.873 23.75 26.1816 22.2559 26.1816 20.4004C26.1816 19.8535 26.0352 19.2871 25.7324 18.7695L15.9668 1.68945C15.3418 0.576172 14.209 0 13.0859 0C11.9531 0 10.8398 0.576172 10.2148 1.68945L0.449219 18.75C0.146484 19.2773 0 19.8535 0 20.4004ZM1.73828 20.4004C1.73828 20.1465 1.80664 19.8535 1.96289 19.5801L11.6992 2.50977C12.0117 1.96289 12.5391 1.71875 13.0859 1.71875L13.0859 22.0605L3.36914 22.0605C2.40234 22.0605 1.73828 21.2695 1.73828 20.4004Z", fillAlpha = 0.85f)
        }
        return _sFTriangleRighthalfFilled!!
    }

private var _sFTriangleRighthalfFilled: ImageVector? = null
