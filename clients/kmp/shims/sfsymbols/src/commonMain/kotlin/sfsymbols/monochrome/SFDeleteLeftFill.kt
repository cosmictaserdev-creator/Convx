package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDeleteLeftFill (monochrome)
 * Viewport: 27.9199 x 22.9785
 */
public val SfSymbols.Monochrome.SFDeleteLeftFill: ImageVector
    get() {
        if (_sFDeleteLeftFill != null) {
            return _sFDeleteLeftFill!!
        }
        _sFDeleteLeftFill = sfIcon(
            name = "Monochrome.SFDeleteLeftFill",
            viewportWidth = 27.9199f,
            viewportHeight = 22.9785f
        ) {
            addSfPath("M26.9238 3.78906L26.9238 19.2188C26.9238 21.7188 25.6348 22.9785 23.1152 22.9785L12.2852 22.9688C10.8398 22.9688 9.64844 22.4902 8.65234 21.4551L1.13281 13.7891C0.332031 12.9785 0 12.2461 0 11.4746C0 10.7031 0.341797 9.98047 1.13281 9.16992L8.64258 1.41602C9.63867 0.371094 10.8301 0.0292969 12.2754 0.0292969L23.1152 0.0292969C25.6348 0.0292969 26.9238 1.28906 26.9238 3.78906ZM19.375 6.64062L15.7422 10.2539L12.1289 6.65039C11.9531 6.48438 11.7578 6.38672 11.5039 6.38672C11.0059 6.38672 10.6055 6.76758 10.6055 7.27539C10.6055 7.50977 10.7031 7.73438 10.8789 7.91016L14.4727 11.5137L10.8789 15.1172C10.7031 15.293 10.6055 15.5078 10.6055 15.752C10.6055 16.25 11.0059 16.6504 11.5039 16.6504C11.7578 16.6504 11.9824 16.5625 12.1582 16.377L15.7422 12.7734L19.3359 16.377C19.502 16.5625 19.7363 16.6504 19.9902 16.6504C20.4883 16.6504 20.8887 16.25 20.8887 15.752C20.8887 15.498 20.8105 15.2832 20.625 15.1074L17.0215 11.5137L20.6348 7.90039C20.8301 7.70508 20.8984 7.5 20.8984 7.25586C20.8984 6.75781 20.498 6.36719 20.0098 6.36719C19.7656 6.36719 19.5605 6.45508 19.375 6.64062Z", fillAlpha = 0.85f)
        }
        return _sFDeleteLeftFill!!
    }

private var _sFDeleteLeftFill: ImageVector? = null
