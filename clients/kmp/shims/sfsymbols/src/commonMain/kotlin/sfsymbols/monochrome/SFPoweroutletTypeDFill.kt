package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPoweroutletTypeDFill (monochrome)
 * Viewport: 24.1406 x 22.9785
 */
public val SfSymbols.Monochrome.SFPoweroutletTypeDFill: ImageVector
    get() {
        if (_sFPoweroutletTypeDFill != null) {
            return _sFPoweroutletTypeDFill!!
        }
        _sFPoweroutletTypeDFill = sfIcon(
            name = "Monochrome.SFPoweroutletTypeDFill",
            viewportWidth = 24.1406f,
            viewportHeight = 22.9785f
        ) {
            addSfPath("M23.7793 3.78906L23.7793 19.2188C23.7793 21.709 22.5098 22.9785 19.9805 22.9785L3.79883 22.9785C1.2793 22.9785 0 21.7188 0 19.2188L0 3.78906C0 1.28906 1.2793 0.0292969 3.79883 0.0292969L19.9805 0.0292969C22.5098 0.0292969 23.7793 1.29883 23.7793 3.78906ZM5.55664 15.918C5.55664 16.9531 6.40625 17.8125 7.44141 17.8125C8.47656 17.8125 9.31641 16.9531 9.31641 15.918C9.31641 14.8926 8.47656 14.043 7.44141 14.043C6.40625 14.043 5.55664 14.8926 5.55664 15.918ZM14.4629 15.918C14.4629 16.9531 15.3125 17.8125 16.3477 17.8125C17.373 17.8125 18.2227 16.9531 18.2227 15.918C18.2227 14.8926 17.373 14.043 16.3477 14.043C15.3125 14.043 14.4629 14.8926 14.4629 15.918ZM9.54102 7.51953C9.54102 8.82812 10.5957 9.86328 11.8945 9.86328C13.1836 9.86328 14.2383 8.82812 14.2383 7.51953C14.2383 6.2207 13.1836 5.16602 11.8945 5.16602C10.5957 5.16602 9.54102 6.2207 9.54102 7.51953Z", fillAlpha = 0.85f)
        }
        return _sFPoweroutletTypeDFill!!
    }

private var _sFPoweroutletTypeDFill: ImageVector? = null
