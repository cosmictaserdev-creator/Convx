package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFOvalPortraitTophalfFilled (monochrome)
 * Viewport: 23.1055 x 29.6582
 */
public val SfSymbols.Monochrome.SFOvalPortraitTophalfFilled: ImageVector
    get() {
        if (_sFOvalPortraitTophalfFilled != null) {
            return _sFOvalPortraitTophalfFilled!!
        }
        _sFOvalPortraitTophalfFilled = sfIcon(
            name = "Monochrome.SFOvalPortraitTophalfFilled",
            viewportWidth = 23.1055f,
            viewportHeight = 29.6582f
        ) {
            addSfPath("M11.377 0C4.6582 0 0 6.12305 0 14.8242C0 23.5156 4.6582 29.6484 11.377 29.6484C18.1055 29.6484 22.7441 23.5352 22.7441 14.8242C22.7441 6.11328 18.1055 0 11.377 0ZM1.72852 14.8242L21.0156 14.8242C21.0156 22.5098 17.0703 27.9102 11.377 27.9102C5.68359 27.9102 1.72852 22.5 1.72852 14.8242Z", fillAlpha = 0.85f)
        }
        return _sFOvalPortraitTophalfFilled!!
    }

private var _sFOvalPortraitTophalfFilled: ImageVector? = null
