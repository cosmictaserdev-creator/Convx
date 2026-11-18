package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPoweroutletTypeCFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFPoweroutletTypeCFill: ImageVector
    get() {
        if (_sFPoweroutletTypeCFill != null) {
            return _sFPoweroutletTypeCFill!!
        }
        _sFPoweroutletTypeCFill = sfIcon(
            name = "Dualtone.SFPoweroutletTypeCFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M7.34375 15.127C6.00586 15.127 4.94141 14.0527 4.94141 12.7246C4.94141 11.3965 6.00586 10.3125 7.34375 10.3125C8.67188 10.3125 9.75586 11.3965 9.75586 12.7246C9.75586 14.0527 8.67188 15.127 7.34375 15.127ZM18.0859 15.127C16.7578 15.127 15.6836 14.0527 15.6836 12.7246C15.6836 11.3965 16.7578 10.3125 18.0859 10.3125C19.4238 10.3125 20.498 11.3965 20.498 12.7246C20.498 14.0527 19.4238 15.127 18.0859 15.127Z", fillAlpha = 0.85f)
        }
        return _sFPoweroutletTypeCFill!!
    }

private var _sFPoweroutletTypeCFill: ImageVector? = null
