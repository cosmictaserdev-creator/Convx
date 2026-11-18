package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFAirplaneCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFAirplaneCircleFill: ImageVector
    get() {
        if (_sFAirplaneCircleFill != null) {
            return _sFAirplaneCircleFill!!
        }
        _sFAirplaneCircleFill = sfIcon(
            name = "Monochrome.SFAirplaneCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7344C25.4395 19.7461 19.7266 25.459 12.7148 25.459C5.71289 25.459 0 19.7461 0 12.7344C0 5.73242 5.71289 0.0195312 12.7148 0.0195312C19.7266 0.0195312 25.4395 5.73242 25.4395 12.7344ZM9.25781 5.75195C9.01367 5.75195 8.86719 5.98633 8.97461 6.24023L11.2598 11.2891L7.95898 11.6504L6.75781 9.47266C6.64062 9.26758 6.47461 9.17969 6.2207 9.17969L5.91797 9.17969C5.68359 9.17969 5.52734 9.32617 5.52734 9.56055L5.52734 15.918C5.52734 16.1523 5.68359 16.3086 5.91797 16.3086L6.2207 16.3086C6.47461 16.3086 6.64062 16.2109 6.75781 16.0156L7.95898 13.8281L11.2598 14.1992L8.97461 19.248C8.86719 19.502 9.01367 19.7363 9.25781 19.7363L10.0488 19.7363C10.2637 19.7363 10.4785 19.6387 10.6348 19.4629L15.1855 14.4922C15.4004 14.2676 15.5078 14.2188 15.8496 14.2188L18.623 14.2188C20 14.2188 21.1133 13.584 21.1133 12.7441C21.1133 11.9043 20 11.2695 18.623 11.2695L15.8496 11.2695C15.5078 11.2695 15.4004 11.2207 15.1855 10.9863L10.6348 6.02539C10.4785 5.84961 10.2637 5.75195 10.0488 5.75195Z", fillAlpha = 0.85f)
        }
        return _sFAirplaneCircleFill!!
    }

private var _sFAirplaneCircleFill: ImageVector? = null
