package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFAirplayVideoCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFAirplayVideoCircleFill: ImageVector
    get() {
        if (_sFAirplayVideoCircleFill != null) {
            return _sFAirplayVideoCircleFill!!
        }
        _sFAirplayVideoCircleFill = sfIcon(
            name = "Monochrome.SFAirplayVideoCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7344C25.4395 19.7461 19.7266 25.459 12.7148 25.459C5.71289 25.459 0 19.7461 0 12.7344C0 5.73242 5.71289 0.0195312 12.7148 0.0195312C19.7266 0.0195312 25.4395 5.73242 25.4395 12.7344ZM12.3535 14.3457L8.56445 18.623C8.30078 18.916 8.50586 19.3457 8.88672 19.3457L16.5332 19.3457C16.9238 19.3457 17.1094 18.916 16.8457 18.623L13.0664 14.3457C12.9004 14.1504 12.5391 14.1504 12.3535 14.3457ZM7.06055 7.29492C6.04492 7.29492 5.33203 8.01758 5.33203 9.01367L5.33203 15.2539C5.33203 16.2695 6.04492 16.9727 7.06055 16.9727L8.73047 16.9727L9.77539 15.8008L7.17773 15.8008C6.82617 15.8008 6.5625 15.5469 6.5625 15.166L6.5625 9.12109C6.5625 8.73047 6.82617 8.4668 7.17773 8.4668L18.2422 8.4668C18.6035 8.4668 18.8574 8.73047 18.8574 9.12109L18.8574 15.166C18.8574 15.5469 18.6035 15.8008 18.2422 15.8008L15.6543 15.8008L16.6797 16.9727L18.3496 16.9727C19.3652 16.9727 20.0879 16.2695 20.0879 15.2539L20.0879 9.01367C20.0879 8.01758 19.3652 7.29492 18.3496 7.29492Z", fillAlpha = 0.85f)
        }
        return _sFAirplayVideoCircleFill!!
    }

private var _sFAirplayVideoCircleFill: ImageVector? = null
