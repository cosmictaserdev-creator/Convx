package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowTriangleheadTurnUpRight (dualtone)
 * Viewport: 18.6914 x 24.1895
 */
public val SfSymbols.Dualtone.SFArrowTriangleheadTurnUpRight: ImageVector
    get() {
        if (_sFArrowTriangleheadTurnUpRight != null) {
            return _sFArrowTriangleheadTurnUpRight!!
        }
        _sFArrowTriangleheadTurnUpRight = sfIcon(
            name = "Dualtone.SFArrowTriangleheadTurnUpRight",
            viewportWidth = 18.6914f,
            viewportHeight = 24.1895f
        ) {
            addSfPath("M13.9648 4.26758L5.92773 4.26758C2.08984 4.26758 0 6.36719 0 10.1953L0 23.2715C0 23.7793 0.410156 24.1895 0.917969 24.1895C1.41602 24.1895 1.82617 23.7793 1.82617 23.2715L1.82617 10.1758C1.82617 7.50977 3.24219 6.10352 5.92773 6.10352L13.9648 6.10352C14.4629 6.10352 14.873 5.69336 14.873 5.18555C14.873 4.6875 14.4629 4.26758 13.9648 4.26758ZM12.8125 2.35352L12.8125 8.03711C12.8125 8.82812 13.3594 9.04297 13.9746 8.60352L17.9395 5.83008C18.457 5.46875 18.4668 4.93164 17.9395 4.56055L13.9844 1.78711C13.3594 1.33789 12.8125 1.55273 12.8125 2.35352Z", fillAlpha = 0.85f)
        }
        return _sFArrowTriangleheadTurnUpRight!!
    }

private var _sFArrowTriangleheadTurnUpRight: ImageVector? = null
