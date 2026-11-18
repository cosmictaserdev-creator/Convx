package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPoweroutletTypeOFill (dualtone)
 * Viewport: 24.1406 x 22.9785
 */
public val SfSymbols.Dualtone.SFPoweroutletTypeOFill: ImageVector
    get() {
        if (_sFPoweroutletTypeOFill != null) {
            return _sFPoweroutletTypeOFill!!
        }
        _sFPoweroutletTypeOFill = sfIcon(
            name = "Dualtone.SFPoweroutletTypeOFill",
            viewportWidth = 24.1406f,
            viewportHeight = 22.9785f
        ) {
            addSfPath("M3.79883 22.9785L19.9805 22.9785C22.5098 22.9785 23.7793 21.709 23.7793 19.2188L23.7793 3.78906C23.7793 1.29883 22.5098 0.0292969 19.9805 0.0292969L3.79883 0.0292969C1.2793 0.0292969 0 1.28906 0 3.78906L0 19.2188C0 21.7188 1.2793 22.9785 3.79883 22.9785Z", fillAlpha = 0.2125f)
            addSfPath("M7.44141 16.8652C6.40625 16.8652 5.55664 16.0254 5.55664 14.9805C5.55664 13.9453 6.40625 13.1055 7.44141 13.1055C8.47656 13.1055 9.31641 13.9453 9.31641 14.9805C9.31641 16.0254 8.47656 16.8652 7.44141 16.8652ZM16.3477 16.8652C15.3125 16.8652 14.4629 16.0254 14.4629 14.9805C14.4629 13.9453 15.3125 13.1055 16.3477 13.1055C17.373 13.1055 18.2227 13.9453 18.2227 14.9805C18.2227 16.0254 17.373 16.8652 16.3477 16.8652ZM11.8945 9.87305C10.8496 9.87305 10.0098 9.0332 10.0098 7.98828C10.0098 6.95312 10.8496 6.11328 11.8945 6.11328C12.9199 6.11328 13.7695 6.95312 13.7695 7.98828C13.7695 9.0332 12.9199 9.87305 11.8945 9.87305Z", fillAlpha = 0.85f)
        }
        return _sFPoweroutletTypeOFill!!
    }

private var _sFPoweroutletTypeOFill: ImageVector? = null
