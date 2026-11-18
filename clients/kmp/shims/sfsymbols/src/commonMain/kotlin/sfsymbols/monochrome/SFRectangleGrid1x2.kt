package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRectangleGrid1x2 (monochrome)
 * Viewport: 27.4609 x 22.9785
 */
public val SfSymbols.Monochrome.SFRectangleGrid1x2: ImageVector
    get() {
        if (_sFRectangleGrid1x2 != null) {
            return _sFRectangleGrid1x2!!
        }
        _sFRectangleGrid1x2 = sfIcon(
            name = "Monochrome.SFRectangleGrid1x2",
            viewportWidth = 27.4609f,
            viewportHeight = 22.9785f
        ) {
            addSfPath("M2.62695 22.9785L24.4727 22.9785C26.2109 22.9785 27.0996 22.1387 27.0996 20.3711L27.0996 15.1758C27.0996 13.4082 26.2109 12.5684 24.4727 12.5684L2.62695 12.5684C0.888672 12.5684 0 13.4082 0 15.1758L0 20.3711C0 22.1387 0.888672 22.9785 2.62695 22.9785ZM2.75391 21.3477C2.01172 21.3477 1.63086 20.9863 1.63086 20.2148L1.63086 15.3418C1.63086 14.5605 2.01172 14.1992 2.75391 14.1992L24.3359 14.1992C25.0879 14.1992 25.4688 14.5605 25.4688 15.3418L25.4688 20.2148C25.4688 20.9863 25.0879 21.3477 24.3359 21.3477Z", fillAlpha = 0.85f)
            addSfPath("M2.62695 10.4297L24.4727 10.4297C26.2109 10.4297 27.0996 9.59961 27.0996 7.82227L27.0996 2.62695C27.0996 0.859375 26.2109 0.0292969 24.4727 0.0292969L2.62695 0.0292969C0.888672 0.0292969 0 0.859375 0 2.62695L0 7.82227C0 9.59961 0.888672 10.4297 2.62695 10.4297ZM2.75391 8.79883C2.01172 8.79883 1.63086 8.4375 1.63086 7.66602L1.63086 2.79297C1.63086 2.01172 2.01172 1.66016 2.75391 1.66016L24.3359 1.66016C25.0879 1.66016 25.4688 2.01172 25.4688 2.79297L25.4688 7.66602C25.4688 8.4375 25.0879 8.79883 24.3359 8.79883Z", fillAlpha = 0.85f)
        }
        return _sFRectangleGrid1x2!!
    }

private var _sFRectangleGrid1x2: ImageVector? = null
