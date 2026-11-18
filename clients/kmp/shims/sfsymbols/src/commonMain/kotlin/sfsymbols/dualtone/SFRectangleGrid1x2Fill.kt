package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRectangleGrid1x2Fill (dualtone)
 * Viewport: 27.4609 x 22.9785
 */
public val SfSymbols.Dualtone.SFRectangleGrid1x2Fill: ImageVector
    get() {
        if (_sFRectangleGrid1x2Fill != null) {
            return _sFRectangleGrid1x2Fill!!
        }
        _sFRectangleGrid1x2Fill = sfIcon(
            name = "Dualtone.SFRectangleGrid1x2Fill",
            viewportWidth = 27.4609f,
            viewportHeight = 22.9785f
        ) {
            addSfPath("M2.62695 22.9785L24.4727 22.9785C26.2109 22.9785 27.0996 22.1387 27.0996 20.3711L27.0996 15.1758C27.0996 13.4082 26.2109 12.5684 24.4727 12.5684L2.62695 12.5684C0.888672 12.5684 0 13.4082 0 15.1758L0 20.3711C0 22.1387 0.888672 22.9785 2.62695 22.9785Z", fillAlpha = 0.85f)
            addSfPath("M2.62695 10.4297L24.4727 10.4297C26.2109 10.4297 27.0996 9.59961 27.0996 7.82227L27.0996 2.62695C27.0996 0.859375 26.2109 0.0292969 24.4727 0.0292969L2.62695 0.0292969C0.888672 0.0292969 0 0.859375 0 2.62695L0 7.82227C0 9.59961 0.888672 10.4297 2.62695 10.4297Z", fillAlpha = 0.85f)
        }
        return _sFRectangleGrid1x2Fill!!
    }

private var _sFRectangleGrid1x2Fill: ImageVector? = null
