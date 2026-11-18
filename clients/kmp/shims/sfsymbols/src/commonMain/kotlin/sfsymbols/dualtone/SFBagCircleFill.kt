package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBagCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFBagCircleFill: ImageVector
    get() {
        if (_sFBagCircleFill != null) {
            return _sFBagCircleFill!!
        }
        _sFBagCircleFill = sfIcon(
            name = "Dualtone.SFBagCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.459C19.7266 25.459 25.4395 19.7461 25.4395 12.7344C25.4395 5.73242 19.7266 0.0195312 12.7148 0.0195312C5.71289 0.0195312 0 5.73242 0 12.7344C0 19.7461 5.71289 25.459 12.7148 25.459Z", fillAlpha = 0.2125f)
            addSfPath("M8.83789 19.0234C7.4707 19.0234 6.76758 18.3398 6.76758 17.0117L6.76758 9.93164C6.76758 8.58398 7.4707 7.91992 8.83789 7.91992L9.99023 7.91992C10.0195 6.54297 11.1719 5.35156 12.7148 5.35156C14.2578 5.35156 15.4199 6.54297 15.4395 7.91992L16.6016 7.91992C17.959 7.91992 18.6621 8.59375 18.6621 9.93164L18.6621 17.0117C18.6621 18.3301 17.9688 19.0234 16.7676 19.0234ZM11.1035 7.91992L14.3262 7.91992C14.3066 6.99219 13.6914 6.40625 12.7148 6.40625C11.7383 6.40625 11.1328 6.99219 11.1035 7.91992Z", fillAlpha = 0.85f)
        }
        return _sFBagCircleFill!!
    }

private var _sFBagCircleFill: ImageVector? = null
