package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPoweroutletTypeEFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFPoweroutletTypeEFill: ImageVector
    get() {
        if (_sFPoweroutletTypeEFill != null) {
            return _sFPoweroutletTypeEFill!!
        }
        _sFPoweroutletTypeEFill = sfIcon(
            name = "Dualtone.SFPoweroutletTypeEFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M12.7148 8.36914C11.8164 8.36914 11.0742 7.62695 11.0742 6.72852C11.0742 5.82031 11.8164 5.07812 12.7148 5.07812C13.623 5.07812 14.3652 5.82031 14.3652 6.72852C14.3652 7.62695 13.623 8.36914 12.7148 8.36914ZM7.03125 15.2051C5.70312 15.2051 4.61914 14.1211 4.61914 12.793C4.61914 11.4648 5.70312 10.3809 7.03125 10.3809C8.36914 10.3809 9.44336 11.4648 9.44336 12.793C9.44336 14.1211 8.36914 15.2051 7.03125 15.2051ZM18.4082 15.2051C17.0703 15.2051 15.9961 14.1211 15.9961 12.793C15.9961 11.4648 17.0703 10.3809 18.4082 10.3809C19.7363 10.3809 20.8105 11.4648 20.8105 12.793C20.8105 14.1211 19.7363 15.2051 18.4082 15.2051Z", fillAlpha = 0.85f)
        }
        return _sFPoweroutletTypeEFill!!
    }

private var _sFPoweroutletTypeEFill: ImageVector? = null
