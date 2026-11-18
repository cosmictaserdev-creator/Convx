package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPoweroutletTypeOFill (monochrome)
 * Viewport: 24.1406 x 22.9785
 */
public val SfSymbols.Monochrome.SFPoweroutletTypeOFill: ImageVector
    get() {
        if (_sFPoweroutletTypeOFill != null) {
            return _sFPoweroutletTypeOFill!!
        }
        _sFPoweroutletTypeOFill = sfIcon(
            name = "Monochrome.SFPoweroutletTypeOFill",
            viewportWidth = 24.1406f,
            viewportHeight = 22.9785f
        ) {
            addSfPath("M23.7793 3.78906L23.7793 19.2188C23.7793 21.709 22.5098 22.9785 19.9805 22.9785L3.79883 22.9785C1.2793 22.9785 0 21.7188 0 19.2188L0 3.78906C0 1.28906 1.2793 0.0292969 3.79883 0.0292969L19.9805 0.0292969C22.5098 0.0292969 23.7793 1.29883 23.7793 3.78906ZM5.55664 14.9805C5.55664 16.0254 6.40625 16.8652 7.44141 16.8652C8.47656 16.8652 9.31641 16.0254 9.31641 14.9805C9.31641 13.9453 8.47656 13.1055 7.44141 13.1055C6.40625 13.1055 5.55664 13.9453 5.55664 14.9805ZM14.4629 14.9805C14.4629 16.0254 15.3125 16.8652 16.3477 16.8652C17.373 16.8652 18.2227 16.0254 18.2227 14.9805C18.2227 13.9453 17.373 13.1055 16.3477 13.1055C15.3125 13.1055 14.4629 13.9453 14.4629 14.9805ZM10.0098 7.98828C10.0098 9.0332 10.8496 9.87305 11.8945 9.87305C12.9199 9.87305 13.7695 9.0332 13.7695 7.98828C13.7695 6.95312 12.9199 6.11328 11.8945 6.11328C10.8496 6.11328 10.0098 6.95312 10.0098 7.98828Z", fillAlpha = 0.85f)
        }
        return _sFPoweroutletTypeOFill!!
    }

private var _sFPoweroutletTypeOFill: ImageVector? = null
