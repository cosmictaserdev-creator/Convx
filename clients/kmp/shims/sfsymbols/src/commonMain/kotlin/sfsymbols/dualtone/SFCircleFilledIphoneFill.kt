package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCircleFilledIphoneFill (dualtone)
 * Viewport: 16.0254 x 26.2402
 */
public val SfSymbols.Dualtone.SFCircleFilledIphoneFill: ImageVector
    get() {
        if (_sFCircleFilledIphoneFill != null) {
            return _sFCircleFilledIphoneFill!!
        }
        _sFCircleFilledIphoneFill = sfIcon(
            name = "Dualtone.SFCircleFilledIphoneFill",
            viewportWidth = 16.0254f,
            viewportHeight = 26.2402f
        ) {
            addSfPath("M0 22.998C0 24.9707 1.28906 26.2207 3.33008 26.2207L12.3242 26.2207C14.3652 26.2207 15.6641 24.9707 15.6641 22.998L15.6641 3.22266C15.6641 1.25 14.3652 0 12.3242 0L3.33008 0C1.28906 0 0 1.25 0 3.22266Z", fillAlpha = 0.2125f)
            addSfPath("M7.83203 17.9004C5.19531 17.9004 3.03711 15.7422 3.03711 13.1055C3.03711 10.4688 5.19531 8.31055 7.83203 8.31055C10.4688 8.31055 12.627 10.4688 12.627 13.1055C12.627 15.7422 10.4688 17.9004 7.83203 17.9004Z", fillAlpha = 0.85f)
        }
        return _sFCircleFilledIphoneFill!!
    }

private var _sFCircleFilledIphoneFill: ImageVector? = null
