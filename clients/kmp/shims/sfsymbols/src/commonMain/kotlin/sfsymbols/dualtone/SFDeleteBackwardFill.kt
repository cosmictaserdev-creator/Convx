package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDeleteBackwardFill (dualtone)
 * Viewport: 27.9199 x 22.9785
 */
public val SfSymbols.Dualtone.SFDeleteBackwardFill: ImageVector
    get() {
        if (_sFDeleteBackwardFill != null) {
            return _sFDeleteBackwardFill!!
        }
        _sFDeleteBackwardFill = sfIcon(
            name = "Dualtone.SFDeleteBackwardFill",
            viewportWidth = 27.9199f,
            viewportHeight = 22.9785f
        ) {
            addSfPath("M23.1152 22.9785C25.6348 22.9785 26.9238 21.7188 26.9238 19.2188L26.9238 3.78906C26.9238 1.28906 25.6348 0.0292969 23.1152 0.0292969L12.2754 0.0292969C10.8301 0.0292969 9.63867 0.371094 8.64258 1.41602L1.13281 9.16992C0.341797 9.98047 0 10.7031 0 11.4746C0 12.2461 0.332031 12.9785 1.13281 13.7891L8.65234 21.4551C9.64844 22.4902 10.8398 22.9688 12.2852 22.9688Z", fillAlpha = 0.2125f)
            addSfPath("M11.5039 16.6504C11.0059 16.6504 10.6055 16.25 10.6055 15.752C10.6055 15.5078 10.7031 15.293 10.8789 15.1172L14.4727 11.5137L10.8789 7.91016C10.7031 7.73438 10.6055 7.50977 10.6055 7.27539C10.6055 6.76758 11.0059 6.38672 11.5039 6.38672C11.7578 6.38672 11.9531 6.48438 12.1289 6.65039L15.7422 10.2539L19.375 6.64062C19.5605 6.45508 19.7656 6.36719 20.0098 6.36719C20.498 6.36719 20.8984 6.75781 20.8984 7.25586C20.8984 7.5 20.8301 7.70508 20.6348 7.90039L17.0215 11.5137L20.625 15.1074C20.8105 15.2832 20.8887 15.498 20.8887 15.752C20.8887 16.25 20.4883 16.6504 19.9902 16.6504C19.7363 16.6504 19.502 16.5625 19.3359 16.377L15.7422 12.7734L12.1582 16.377C11.9824 16.5625 11.7578 16.6504 11.5039 16.6504Z", fillAlpha = 0.85f)
        }
        return _sFDeleteBackwardFill!!
    }

private var _sFDeleteBackwardFill: ImageVector? = null
