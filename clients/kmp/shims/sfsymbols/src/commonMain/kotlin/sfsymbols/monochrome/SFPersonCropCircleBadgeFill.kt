package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPersonCropCircleBadgeFill (monochrome)
 * Viewport: 38.4082 x 28.8574
 */
public val SfSymbols.Monochrome.SFPersonCropCircleBadgeFill: ImageVector
    get() {
        if (_sFPersonCropCircleBadgeFill != null) {
            return _sFPersonCropCircleBadgeFill!!
        }
        _sFPersonCropCircleBadgeFill = sfIcon(
            name = "Monochrome.SFPersonCropCircleBadgeFill",
            viewportWidth = 38.4082f,
            viewportHeight = 28.8574f
        ) {
            addSfPath("M31.748 14.4238C31.748 21.4258 26.0352 27.1387 19.0234 27.1387C17.1898 27.1387 15.4446 26.7469 13.8717 26.0347C14.2314 25.6148 14.5454 25.1554 14.8069 24.6649C16.1163 25.2089 17.5563 25.5078 19.0234 25.5078C22.0605 25.5078 25 24.2188 26.9141 22.1777C25.5273 19.9805 22.4609 18.6719 19.0234 18.6719C17.7822 18.6719 16.5935 18.8421 15.518 19.1634C14.6942 15.7057 11.561 13.1152 7.87109 13.1152C7.35713 13.1152 6.85397 13.1656 6.36738 13.265C6.94995 6.79254 12.4121 1.69922 19.0234 1.69922C26.0352 1.69922 31.748 7.41211 31.748 14.4238ZM14.7168 11.7773C14.7168 14.4824 16.6016 16.5137 19.0234 16.5332C21.4453 16.5527 23.3301 14.4824 23.3301 11.7773C23.3301 9.23828 21.4258 7.12891 19.0234 7.12891C16.6211 7.12891 14.6973 9.23828 14.7168 11.7773Z", fillAlpha = 0.85f)
            addSfPath("M7.87109 27.3047C11.2988 27.3047 14.1895 24.4434 14.1895 20.9863C14.1895 17.5195 11.3379 14.668 7.87109 14.668C4.41406 14.668 1.55273 17.5293 1.55273 20.9863C1.55273 24.4629 4.41406 27.3047 7.87109 27.3047Z", fillAlpha = 0.85f)
        }
        return _sFPersonCropCircleBadgeFill!!
    }

private var _sFPersonCropCircleBadgeFill: ImageVector? = null
