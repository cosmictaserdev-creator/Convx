package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCircleBadgeMinusFill (monochrome)
 * Viewport: 37.666 x 28.8574
 */
public val SfSymbols.Monochrome.SFCircleBadgeMinusFill: ImageVector
    get() {
        if (_sFCircleBadgeMinusFill != null) {
            return _sFCircleBadgeMinusFill!!
        }
        _sFCircleBadgeMinusFill = sfIcon(
            name = "Monochrome.SFCircleBadgeMinusFill",
            viewportWidth = 37.666f,
            viewportHeight = 28.8574f
        ) {
            addSfPath("M31.3183 13.2677C30.8254 13.1668 30.3157 13.1152 29.7949 13.1152C25.4785 13.1152 21.9141 16.6699 21.9141 20.9863C21.9141 22.9101 22.617 24.6794 23.7808 26.0487C22.2138 26.7518 20.4771 27.1387 18.6523 27.1387C11.6504 27.1387 5.9375 21.4258 5.9375 14.4238C5.9375 7.41211 11.6504 1.69922 18.6523 1.69922C25.2738 1.69922 30.737 6.79392 31.3183 13.2677Z", fillAlpha = 0.85f)
            addSfPath("M36.1035 20.9863C36.1035 24.4434 33.2227 27.3047 29.7949 27.3047C26.3281 27.3047 23.4766 24.4629 23.4766 20.9863C23.4766 17.5293 26.3281 14.668 29.7949 14.668C33.252 14.668 36.1035 17.5195 36.1035 20.9863ZM26.5039 20.2734C26.123 20.2734 25.8008 20.6055 25.8008 20.9863C25.8008 21.3574 26.123 21.6992 26.5039 21.6992L33.0664 21.6992C33.457 21.6992 33.7793 21.3574 33.7793 20.9863C33.7793 20.6055 33.457 20.2734 33.0664 20.2734Z", fillAlpha = 0.85f)
        }
        return _sFCircleBadgeMinusFill!!
    }

private var _sFCircleBadgeMinusFill: ImageVector? = null
