package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPoweroutletTypeDFill (dualtone)
 * Viewport: 24.1406 x 22.9785
 */
public val SfSymbols.Dualtone.SFPoweroutletTypeDFill: ImageVector
    get() {
        if (_sFPoweroutletTypeDFill != null) {
            return _sFPoweroutletTypeDFill!!
        }
        _sFPoweroutletTypeDFill = sfIcon(
            name = "Dualtone.SFPoweroutletTypeDFill",
            viewportWidth = 24.1406f,
            viewportHeight = 22.9785f
        ) {
            addSfPath("M3.79883 22.9785L19.9805 22.9785C22.5098 22.9785 23.7793 21.709 23.7793 19.2188L23.7793 3.78906C23.7793 1.29883 22.5098 0.0292969 19.9805 0.0292969L3.79883 0.0292969C1.2793 0.0292969 0 1.28906 0 3.78906L0 19.2188C0 21.7188 1.2793 22.9785 3.79883 22.9785Z", fillAlpha = 0.2125f)
            addSfPath("M11.8945 9.86328C10.5957 9.86328 9.54102 8.82812 9.54102 7.51953C9.54102 6.2207 10.5957 5.16602 11.8945 5.16602C13.1836 5.16602 14.2383 6.2207 14.2383 7.51953C14.2383 8.82812 13.1836 9.86328 11.8945 9.86328ZM7.44141 17.8125C6.40625 17.8125 5.55664 16.9531 5.55664 15.918C5.55664 14.8926 6.40625 14.043 7.44141 14.043C8.47656 14.043 9.31641 14.8926 9.31641 15.918C9.31641 16.9531 8.47656 17.8125 7.44141 17.8125ZM16.3477 17.8125C15.3125 17.8125 14.4629 16.9531 14.4629 15.918C14.4629 14.8926 15.3125 14.043 16.3477 14.043C17.373 14.043 18.2227 14.8926 18.2227 15.918C18.2227 16.9531 17.373 17.8125 16.3477 17.8125Z", fillAlpha = 0.85f)
        }
        return _sFPoweroutletTypeDFill!!
    }

private var _sFPoweroutletTypeDFill: ImageVector? = null
