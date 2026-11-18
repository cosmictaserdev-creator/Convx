package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowTurnUpLeft (dualtone)
 * Viewport: 23.9648 x 19.5801
 */
public val SfSymbols.Dualtone.SFArrowTurnUpLeft: ImageVector
    get() {
        if (_sFArrowTurnUpLeft != null) {
            return _sFArrowTurnUpLeft!!
        }
        _sFArrowTurnUpLeft = sfIcon(
            name = "Dualtone.SFArrowTurnUpLeft",
            viewportWidth = 23.9648f,
            viewportHeight = 19.5801f
        ) {
            addSfPath("M1.96289 9.47266L6.76758 5.0293L10.1465 1.60156C10.3223 1.43555 10.4102 1.18164 10.4102 0.927734C10.4102 0.400391 10 0 9.47266 0C9.21875 0 8.98438 0.0976562 8.7207 0.351562L0.332031 8.74023C0.107422 8.95508 0 9.20898 0 9.47266C0 9.73633 0.107422 9.98047 0.332031 10.1953L8.7793 18.6426C8.98438 18.8477 9.21875 18.9355 9.47266 18.9355C10 18.9355 10.4102 18.5449 10.4102 18.0078C10.4102 17.7539 10.3125 17.5 10.1465 17.334L6.76758 13.9062ZM1.97266 10.2637L5.81055 10.4199L15.2539 10.4199C19.9121 10.4199 21.6699 12.3535 21.6699 17.0703C21.6602 17.6953 21.6504 18.1934 21.6406 18.5449C21.6406 19.1113 22.0312 19.502 22.5977 19.502C23.1543 19.502 23.418 19.1309 23.5059 18.6914C23.584 18.3496 23.6035 17.7051 23.6035 16.9727C23.6035 11.0449 21.1523 8.51562 15.3418 8.51562L5.81055 8.52539L1.99219 8.67188C1.55273 8.69141 1.19141 9.0332 1.19141 9.47266C1.19141 9.90234 1.54297 10.2441 1.97266 10.2637Z", fillAlpha = 0.85f)
        }
        return _sFArrowTurnUpLeft!!
    }

private var _sFArrowTurnUpLeft: ImageVector? = null
