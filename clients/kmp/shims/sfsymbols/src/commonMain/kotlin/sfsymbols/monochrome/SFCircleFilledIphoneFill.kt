package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCircleFilledIphoneFill (monochrome)
 * Viewport: 16.0254 x 26.2402
 */
public val SfSymbols.Monochrome.SFCircleFilledIphoneFill: ImageVector
    get() {
        if (_sFCircleFilledIphoneFill != null) {
            return _sFCircleFilledIphoneFill!!
        }
        _sFCircleFilledIphoneFill = sfIcon(
            name = "Monochrome.SFCircleFilledIphoneFill",
            viewportWidth = 16.0254f,
            viewportHeight = 26.2402f
        ) {
            addSfPath("M15.6641 3.22266L15.6641 22.998C15.6641 24.9707 14.3652 26.2207 12.3242 26.2207L3.33008 26.2207C1.28906 26.2207 0 24.9707 0 22.998L0 3.22266C0 1.25 1.28906 0 3.33008 0L12.3242 0C14.3652 0 15.6641 1.25 15.6641 3.22266ZM3.03711 13.1055C3.03711 15.7422 5.19531 17.9004 7.83203 17.9004C10.4688 17.9004 12.627 15.7422 12.627 13.1055C12.627 10.4688 10.4688 8.31055 7.83203 8.31055C5.19531 8.31055 3.03711 10.4688 3.03711 13.1055Z", fillAlpha = 0.85f)
        }
        return _sFCircleFilledIphoneFill!!
    }

private var _sFCircleFilledIphoneFill: ImageVector? = null
