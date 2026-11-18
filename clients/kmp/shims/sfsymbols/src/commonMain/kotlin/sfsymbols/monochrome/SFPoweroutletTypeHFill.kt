package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPoweroutletTypeHFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFPoweroutletTypeHFill: ImageVector
    get() {
        if (_sFPoweroutletTypeHFill != null) {
            return _sFPoweroutletTypeHFill!!
        }
        _sFPoweroutletTypeHFill = sfIcon(
            name = "Monochrome.SFPoweroutletTypeHFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM10.3125 18.1445C10.3125 19.4824 11.3867 20.5566 12.7148 20.5566C14.043 20.5566 15.127 19.4824 15.127 18.1445C15.127 16.8164 14.043 15.7324 12.7148 15.7324C11.3867 15.7324 10.3125 16.8164 10.3125 18.1445ZM4.67773 12.1777C4.67773 13.5059 5.75195 14.5898 7.08984 14.5898C8.41797 14.5898 9.50195 13.5059 9.50195 12.1777C9.50195 10.8398 8.41797 9.76562 7.08984 9.76562C5.75195 9.76562 4.67773 10.8398 4.67773 12.1777ZM15.9375 12.1777C15.9375 13.5059 17.0215 14.5898 18.3496 14.5898C19.6875 14.5898 20.752 13.5059 20.752 12.1777C20.752 10.8398 19.6875 9.76562 18.3496 9.76562C17.0215 9.76562 15.9375 10.8398 15.9375 12.1777Z", fillAlpha = 0.85f)
        }
        return _sFPoweroutletTypeHFill!!
    }

private var _sFPoweroutletTypeHFill: ImageVector? = null
