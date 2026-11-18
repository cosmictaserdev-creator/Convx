package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPoweroutletTypeC (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFPoweroutletTypeC: ImageVector
    get() {
        if (_sFPoweroutletTypeC != null) {
            return _sFPoweroutletTypeC!!
        }
        _sFPoweroutletTypeC = sfIcon(
            name = "Monochrome.SFPoweroutletTypeC",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M7.46094 15.0684C8.75977 15.0684 9.81445 14.0234 9.81445 12.7246C9.81445 11.4258 8.75977 10.3711 7.46094 10.3711C6.17188 10.3711 5.11719 11.4258 5.11719 12.7246C5.11719 14.0234 6.17188 15.0684 7.46094 15.0684ZM17.9688 15.0684C19.2676 15.0684 20.3223 14.0234 20.3223 12.7246C20.3223 11.4258 19.2676 10.3711 17.9688 10.3711C16.6699 10.3711 15.6152 11.4258 15.6152 12.7246C15.6152 14.0234 16.6699 15.0684 17.9688 15.0684Z", fillAlpha = 0.85f)
        }
        return _sFPoweroutletTypeC!!
    }

private var _sFPoweroutletTypeC: ImageVector? = null
