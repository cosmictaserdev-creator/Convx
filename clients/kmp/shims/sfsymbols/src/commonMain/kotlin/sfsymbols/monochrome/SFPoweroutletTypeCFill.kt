package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPoweroutletTypeCFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFPoweroutletTypeCFill: ImageVector
    get() {
        if (_sFPoweroutletTypeCFill != null) {
            return _sFPoweroutletTypeCFill!!
        }
        _sFPoweroutletTypeCFill = sfIcon(
            name = "Monochrome.SFPoweroutletTypeCFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM4.94141 12.7246C4.94141 14.0527 6.00586 15.127 7.34375 15.127C8.67188 15.127 9.75586 14.0527 9.75586 12.7246C9.75586 11.3965 8.67188 10.3125 7.34375 10.3125C6.00586 10.3125 4.94141 11.3965 4.94141 12.7246ZM15.6836 12.7246C15.6836 14.0527 16.7578 15.127 18.0859 15.127C19.4238 15.127 20.498 14.0527 20.498 12.7246C20.498 11.3965 19.4238 10.3125 18.0859 10.3125C16.7578 10.3125 15.6836 11.3965 15.6836 12.7246Z", fillAlpha = 0.85f)
        }
        return _sFPoweroutletTypeCFill!!
    }

private var _sFPoweroutletTypeCFill: ImageVector? = null
