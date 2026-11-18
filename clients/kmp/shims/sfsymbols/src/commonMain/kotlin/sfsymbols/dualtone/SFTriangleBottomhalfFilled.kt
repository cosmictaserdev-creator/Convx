package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTriangleBottomhalfFilled (dualtone)
 * Viewport: 26.543 x 23.8379
 */
public val SfSymbols.Dualtone.SFTriangleBottomhalfFilled: ImageVector
    get() {
        if (_sFTriangleBottomhalfFilled != null) {
            return _sFTriangleBottomhalfFilled!!
        }
        _sFTriangleBottomhalfFilled = sfIcon(
            name = "Dualtone.SFTriangleBottomhalfFilled",
            viewportWidth = 26.543f,
            viewportHeight = 23.8379f
        ) {
            addSfPath("M0 20.4004C0 22.2559 1.30859 23.75 3.34961 23.75L22.8418 23.75C24.873 23.75 26.1816 22.2559 26.1816 20.4004C26.1816 19.8535 26.0352 19.2773 25.7324 18.75L15.9668 1.68945C15.3418 0.576172 14.2285 0 13.0957 0C11.9629 0 10.8398 0.576172 10.2148 1.68945L0.439453 18.7695C0.146484 19.2871 0 19.8535 0 20.4004ZM5.6543 13.0664L11.6895 2.50977C11.9922 1.97266 12.5488 1.71875 13.0957 1.71875C13.6426 1.71875 14.1699 1.96289 14.4824 2.50977L20.498 13.0664Z", fillAlpha = 0.85f)
        }
        return _sFTriangleBottomhalfFilled!!
    }

private var _sFTriangleBottomhalfFilled: ImageVector? = null
