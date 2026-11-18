package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowTurnDownLeft (monochrome)
 * Viewport: 23.9648 x 20.6934
 */
public val SfSymbols.Monochrome.SFArrowTurnDownLeft: ImageVector
    get() {
        if (_sFArrowTurnDownLeft != null) {
            return _sFArrowTurnDownLeft!!
        }
        _sFArrowTurnDownLeft = sfIcon(
            name = "Monochrome.SFArrowTurnDownLeft",
            viewportWidth = 23.9648f,
            viewportHeight = 20.6934f
        ) {
            addSfPath("M1.96289 10.0293L6.76758 5.58594L10.1465 2.1582C10.3125 1.99219 10.4102 1.73828 10.4102 1.48438C10.4102 0.957031 10 0.556641 9.47266 0.556641C9.21875 0.556641 8.98438 0.654297 8.7793 0.859375L0.332031 9.29688C0.107422 9.51172 0 9.76562 0 10.0293C0 10.293 0.107422 10.5371 0.332031 10.752L8.7207 19.1406C8.98438 19.3945 9.21875 19.4922 9.47266 19.4922C10 19.4922 10.4102 19.1016 10.4102 18.5645C10.4102 18.3105 10.3223 18.0664 10.1465 17.8906L6.76758 14.4629ZM1.97266 9.23828C1.54297 9.25781 1.19141 9.59961 1.19141 10.0293C1.19141 10.4688 1.55273 10.8105 1.99219 10.8301L5.81055 10.9766L15.3418 10.9766C21.1523 10.9766 23.6035 8.45703 23.6035 2.51953C23.6035 1.78711 23.584 1.15234 23.5059 0.810547C23.418 0.371094 23.1543 0 22.5977 0C22.0312 0 21.6406 0.390625 21.6406 0.947266C21.6504 1.29883 21.6602 1.79688 21.6699 2.42188C21.6699 7.14844 19.9121 9.07227 15.2539 9.07227L5.81055 9.08203Z", fillAlpha = 0.85f)
        }
        return _sFArrowTurnDownLeft!!
    }

private var _sFArrowTurnDownLeft: ImageVector? = null
