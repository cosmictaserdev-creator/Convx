package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCircleFilledIpadLandscapeFill (dualtone)
 * Viewport: 29.9512 x 22.9785
 */
public val SfSymbols.Dualtone.SFCircleFilledIpadLandscapeFill: ImageVector
    get() {
        if (_sFCircleFilledIpadLandscapeFill != null) {
            return _sFCircleFilledIpadLandscapeFill!!
        }
        _sFCircleFilledIpadLandscapeFill = sfIcon(
            name = "Dualtone.SFCircleFilledIpadLandscapeFill",
            viewportWidth = 29.9512f,
            viewportHeight = 22.9785f
        ) {
            addSfPath("M3.79883 22.9785L25.7812 22.9785C28.3105 22.9785 29.5898 21.709 29.5898 19.2188L29.5898 3.78906C29.5898 1.29883 28.3105 0.0292969 25.7812 0.0292969L3.79883 0.0292969C1.2793 0.0292969 0 1.28906 0 3.78906L0 19.2188C0 21.7188 1.2793 22.9785 3.79883 22.9785Z", fillAlpha = 0.2125f)
            addSfPath("M14.7949 17.9004C11.2598 17.9004 8.37891 15.0391 8.37891 11.4844C8.37891 7.95898 11.2598 5.08789 14.7949 5.08789C18.3203 5.08789 21.2109 7.95898 21.2109 11.4844C21.2109 15.0391 18.3203 17.9004 14.7949 17.9004Z", fillAlpha = 0.85f)
        }
        return _sFCircleFilledIpadLandscapeFill!!
    }

private var _sFCircleFilledIpadLandscapeFill: ImageVector? = null
