package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFAirplaneCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFAirplaneCircleFill: ImageVector
    get() {
        if (_sFAirplaneCircleFill != null) {
            return _sFAirplaneCircleFill!!
        }
        _sFAirplaneCircleFill = sfIcon(
            name = "Dualtone.SFAirplaneCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.459C19.7266 25.459 25.4395 19.7461 25.4395 12.7344C25.4395 5.73242 19.7266 0.0195312 12.7148 0.0195312C5.71289 0.0195312 0 5.73242 0 12.7344C0 19.7461 5.71289 25.459 12.7148 25.459Z", fillAlpha = 0.2125f)
            addSfPath("M21.1133 12.7441C21.1133 13.584 20 14.2188 18.623 14.2188L15.8496 14.2188C15.5078 14.2188 15.4004 14.2676 15.1855 14.4922L10.6348 19.4629C10.4785 19.6387 10.2637 19.7363 10.0488 19.7363L9.25781 19.7363C9.01367 19.7363 8.86719 19.502 8.97461 19.248L11.2598 14.1992L7.95898 13.8281L6.75781 16.0156C6.64062 16.2109 6.47461 16.3086 6.2207 16.3086L5.91797 16.3086C5.68359 16.3086 5.52734 16.1523 5.52734 15.918L5.52734 9.56055C5.52734 9.32617 5.68359 9.17969 5.91797 9.17969L6.2207 9.17969C6.47461 9.17969 6.64062 9.26758 6.75781 9.47266L7.95898 11.6504L11.2598 11.2891L8.97461 6.24023C8.86719 5.98633 9.01367 5.75195 9.25781 5.75195L10.0488 5.75195C10.2637 5.75195 10.4785 5.84961 10.6348 6.02539L15.1855 10.9863C15.4004 11.2207 15.5078 11.2695 15.8496 11.2695L18.623 11.2695C20 11.2695 21.1133 11.9043 21.1133 12.7441Z", fillAlpha = 0.85f)
        }
        return _sFAirplaneCircleFill!!
    }

private var _sFAirplaneCircleFill: ImageVector? = null
