package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFAirplayVideoCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFAirplayVideoCircleFill: ImageVector
    get() {
        if (_sFAirplayVideoCircleFill != null) {
            return _sFAirplayVideoCircleFill!!
        }
        _sFAirplayVideoCircleFill = sfIcon(
            name = "Dualtone.SFAirplayVideoCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.459C19.7266 25.459 25.4395 19.7461 25.4395 12.7344C25.4395 5.73242 19.7266 0.0195312 12.7148 0.0195312C5.71289 0.0195312 0 5.73242 0 12.7344C0 19.7461 5.71289 25.459 12.7148 25.459Z", fillAlpha = 0.2125f)
            addSfPath("M16.5332 19.3457L8.88672 19.3457C8.50586 19.3457 8.30078 18.916 8.56445 18.623L12.3535 14.3457C12.5391 14.1504 12.9004 14.1504 13.0664 14.3457L16.8457 18.623C17.1094 18.916 16.9238 19.3457 16.5332 19.3457ZM18.3496 7.29492C19.3652 7.29492 20.0879 8.01758 20.0879 9.01367L20.0879 15.2539C20.0879 16.2695 19.3652 16.9727 18.3496 16.9727L16.6797 16.9727L15.6543 15.8008L18.2422 15.8008C18.6035 15.8008 18.8574 15.5469 18.8574 15.166L18.8574 9.12109C18.8574 8.73047 18.6035 8.4668 18.2422 8.4668L7.17773 8.4668C6.82617 8.4668 6.5625 8.73047 6.5625 9.12109L6.5625 15.166C6.5625 15.5469 6.82617 15.8008 7.17773 15.8008L9.77539 15.8008L8.73047 16.9727L7.06055 16.9727C6.04492 16.9727 5.33203 16.2695 5.33203 15.2539L5.33203 9.01367C5.33203 8.01758 6.04492 7.29492 7.06055 7.29492Z", fillAlpha = 0.85f)
        }
        return _sFAirplayVideoCircleFill!!
    }

private var _sFAirplayVideoCircleFill: ImageVector? = null
