package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFGreaterthanCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFGreaterthanCircleFill: ImageVector
    get() {
        if (_sFGreaterthanCircleFill != null) {
            return _sFGreaterthanCircleFill!!
        }
        _sFGreaterthanCircleFill = sfIcon(
            name = "Dualtone.SFGreaterthanCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M9.43359 17.8516C8.89648 17.8516 8.50586 17.4609 8.50586 16.9531C8.50586 16.5332 8.70117 16.2695 9.16016 16.0547L15.7715 12.7344L15.7715 12.6562L9.16016 9.28711C8.71094 9.0625 8.50586 8.78906 8.50586 8.36914C8.50586 7.87109 8.88672 7.49023 9.41406 7.49023C9.6582 7.49023 9.79492 7.53906 9.98047 7.62695L17.5684 11.7383C18.0078 11.9824 18.2227 12.3047 18.2227 12.7246C18.2227 13.1934 18.0273 13.4766 17.5684 13.7109L9.98047 17.7051C9.80469 17.793 9.6582 17.8516 9.43359 17.8516Z", fillAlpha = 0.85f)
        }
        return _sFGreaterthanCircleFill!!
    }

private var _sFGreaterthanCircleFill: ImageVector? = null
