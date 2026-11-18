package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFOvalPortraitRighthalfFilled (dualtone)
 * Viewport: 23.1055 x 29.6582
 */
public val SfSymbols.Dualtone.SFOvalPortraitRighthalfFilled: ImageVector
    get() {
        if (_sFOvalPortraitRighthalfFilled != null) {
            return _sFOvalPortraitRighthalfFilled!!
        }
        _sFOvalPortraitRighthalfFilled = sfIcon(
            name = "Dualtone.SFOvalPortraitRighthalfFilled",
            viewportWidth = 23.1055f,
            viewportHeight = 29.6582f
        ) {
            addSfPath("M11.3672 0C4.63867 0 0 6.11328 0 14.8242C0 23.5352 4.63867 29.6484 11.3672 29.6484C18.0957 29.6484 22.7441 23.5156 22.7441 14.8242C22.7441 6.12305 18.0957 0 11.3672 0ZM11.3672 1.72852L11.3672 27.9102C5.67383 27.9102 1.72852 22.5098 1.72852 14.8242C1.72852 7.13867 5.67383 1.72852 11.3672 1.72852Z", fillAlpha = 0.85f)
        }
        return _sFOvalPortraitRighthalfFilled!!
    }

private var _sFOvalPortraitRighthalfFilled: ImageVector? = null
