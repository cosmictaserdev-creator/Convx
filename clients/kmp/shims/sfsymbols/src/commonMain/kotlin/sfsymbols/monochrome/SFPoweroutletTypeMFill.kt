package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPoweroutletTypeMFill (monochrome)
 * Viewport: 24.1406 x 22.9785
 */
public val SfSymbols.Monochrome.SFPoweroutletTypeMFill: ImageVector
    get() {
        if (_sFPoweroutletTypeMFill != null) {
            return _sFPoweroutletTypeMFill!!
        }
        _sFPoweroutletTypeMFill = sfIcon(
            name = "Monochrome.SFPoweroutletTypeMFill",
            viewportWidth = 24.1406f,
            viewportHeight = 22.9785f
        ) {
            addSfPath("M23.7793 3.78906L23.7793 19.2188C23.7793 21.709 22.5098 22.9785 19.9805 22.9785L3.79883 22.9785C1.2793 22.9785 0 21.7188 0 19.2188L0 3.78906C0 1.28906 1.2793 0.0292969 3.79883 0.0292969L19.9805 0.0292969C22.5098 0.0292969 23.7793 1.29883 23.7793 3.78906ZM5.55664 15.6738C5.55664 16.7188 6.40625 17.5586 7.44141 17.5586C8.47656 17.5586 9.31641 16.7188 9.31641 15.6738C9.31641 14.6484 8.47656 13.7988 7.44141 13.7988C6.40625 13.7988 5.55664 14.6484 5.55664 15.6738ZM14.4629 15.6738C14.4629 16.7188 15.3125 17.5586 16.3477 17.5586C17.373 17.5586 18.2227 16.7188 18.2227 15.6738C18.2227 14.6484 17.373 13.7988 16.3477 13.7988C15.3125 13.7988 14.4629 14.6484 14.4629 15.6738ZM10.0098 7.27539C10.0098 8.30078 10.8496 9.16016 11.8945 9.16016C12.9199 9.16016 13.7695 8.30078 13.7695 7.27539C13.7695 6.24023 12.9199 5.40039 11.8945 5.40039C10.8496 5.40039 10.0098 6.24023 10.0098 7.27539Z", fillAlpha = 0.85f)
        }
        return _sFPoweroutletTypeMFill!!
    }

private var _sFPoweroutletTypeMFill: ImageVector? = null
