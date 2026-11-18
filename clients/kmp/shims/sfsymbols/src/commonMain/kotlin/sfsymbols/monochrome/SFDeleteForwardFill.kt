package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDeleteForwardFill (monochrome)
 * Viewport: 27.1973 x 22.9785
 */
public val SfSymbols.Monochrome.SFDeleteForwardFill: ImageVector
    get() {
        if (_sFDeleteForwardFill != null) {
            return _sFDeleteForwardFill!!
        }
        _sFDeleteForwardFill = sfIcon(
            name = "Monochrome.SFDeleteForwardFill",
            viewportWidth = 27.1973f,
            viewportHeight = 22.9785f
        ) {
            addSfPath("M18.5547 1.41602L26.0645 9.16992C26.8555 9.98047 27.1973 10.7031 27.1973 11.4746C27.1973 12.2461 26.8652 12.9785 26.0645 13.7891L18.5449 21.4551C17.5488 22.4902 16.3574 22.9688 14.9121 22.9688L4.08203 22.9785C1.5625 22.9785 0.273438 21.7188 0.273438 19.2188L0.273438 3.78906C0.273438 1.28906 1.5625 0.0292969 4.08203 0.0292969L14.9219 0.0292969C16.3672 0.0292969 17.5586 0.371094 18.5547 1.41602ZM15.0977 6.64062L11.4746 10.2539L7.86133 6.65039C7.67578 6.48438 7.48047 6.38672 7.22656 6.38672C6.72852 6.38672 6.33789 6.76758 6.33789 7.27539C6.33789 7.50977 6.42578 7.73438 6.60156 7.91016L10.2051 11.5137L6.60156 15.1172C6.42578 15.293 6.33789 15.5078 6.33789 15.752C6.33789 16.25 6.72852 16.6504 7.22656 16.6504C7.48047 16.6504 7.70508 16.5625 7.89062 16.377L11.4746 12.7734L15.0586 16.377C15.2344 16.5625 15.459 16.6504 15.7129 16.6504C16.2207 16.6504 16.6113 16.25 16.6113 15.752C16.6113 15.498 16.5332 15.2832 16.3477 15.1074L12.7441 11.5137L16.3574 7.90039C16.5527 7.70508 16.6211 7.5 16.6211 7.25586C16.6211 6.75781 16.2305 6.36719 15.7324 6.36719C15.4883 6.36719 15.293 6.45508 15.0977 6.64062Z", fillAlpha = 0.85f)
        }
        return _sFDeleteForwardFill!!
    }

private var _sFDeleteForwardFill: ImageVector? = null
