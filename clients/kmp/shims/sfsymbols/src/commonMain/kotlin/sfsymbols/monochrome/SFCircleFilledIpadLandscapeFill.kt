package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCircleFilledIpadLandscapeFill (monochrome)
 * Viewport: 29.9512 x 22.9785
 */
public val SfSymbols.Monochrome.SFCircleFilledIpadLandscapeFill: ImageVector
    get() {
        if (_sFCircleFilledIpadLandscapeFill != null) {
            return _sFCircleFilledIpadLandscapeFill!!
        }
        _sFCircleFilledIpadLandscapeFill = sfIcon(
            name = "Monochrome.SFCircleFilledIpadLandscapeFill",
            viewportWidth = 29.9512f,
            viewportHeight = 22.9785f
        ) {
            addSfPath("M29.5898 3.78906L29.5898 19.2188C29.5898 21.709 28.3105 22.9785 25.7812 22.9785L3.79883 22.9785C1.2793 22.9785 0 21.7188 0 19.2188L0 3.78906C0 1.28906 1.2793 0.0292969 3.79883 0.0292969L25.7812 0.0292969C28.3105 0.0292969 29.5898 1.29883 29.5898 3.78906ZM8.37891 11.4844C8.37891 15.0391 11.2598 17.9004 14.7949 17.9004C18.3203 17.9004 21.2109 15.0391 21.2109 11.4844C21.2109 7.95898 18.3203 5.08789 14.7949 5.08789C11.2598 5.08789 8.37891 7.95898 8.37891 11.4844Z", fillAlpha = 0.85f)
        }
        return _sFCircleFilledIpadLandscapeFill!!
    }

private var _sFCircleFilledIpadLandscapeFill: ImageVector? = null
