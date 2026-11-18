package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBagCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFBagCircleFill: ImageVector
    get() {
        if (_sFBagCircleFill != null) {
            return _sFBagCircleFill!!
        }
        _sFBagCircleFill = sfIcon(
            name = "Monochrome.SFBagCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7344C25.4395 19.7461 19.7266 25.459 12.7148 25.459C5.71289 25.459 0 19.7461 0 12.7344C0 5.73242 5.71289 0.0195312 12.7148 0.0195312C19.7266 0.0195312 25.4395 5.73242 25.4395 12.7344ZM9.99023 7.91992L8.83789 7.91992C7.4707 7.91992 6.76758 8.58398 6.76758 9.93164L6.76758 17.0117C6.76758 18.3398 7.4707 19.0234 8.83789 19.0234L16.7676 19.0234C17.9688 19.0234 18.6621 18.3301 18.6621 17.0117L18.6621 9.93164C18.6621 8.59375 17.959 7.91992 16.6016 7.91992L15.4395 7.91992C15.4199 6.54297 14.2578 5.35156 12.7148 5.35156C11.1719 5.35156 10.0195 6.54297 9.99023 7.91992ZM14.3262 7.91992L11.1035 7.91992C11.1328 6.99219 11.7383 6.40625 12.7148 6.40625C13.6914 6.40625 14.3066 6.99219 14.3262 7.91992Z", fillAlpha = 0.85f)
        }
        return _sFBagCircleFill!!
    }

private var _sFBagCircleFill: ImageVector? = null
