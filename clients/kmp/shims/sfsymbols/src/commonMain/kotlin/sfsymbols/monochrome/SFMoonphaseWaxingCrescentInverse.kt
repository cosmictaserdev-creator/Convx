package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMoonphaseWaxingCrescentInverse (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFMoonphaseWaxingCrescentInverse: ImageVector
    get() {
        if (_sFMoonphaseWaxingCrescentInverse != null) {
            return _sFMoonphaseWaxingCrescentInverse!!
        }
        _sFMoonphaseWaxingCrescentInverse = sfIcon(
            name = "Monochrome.SFMoonphaseWaxingCrescentInverse",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M17.4121 12.7344C17.4121 17.5684 15.0879 21.5527 11.2988 23.5156C5.94727 22.8418 1.80664 18.2812 1.81641 12.7246C1.82617 7.19727 5.9082 2.65625 11.2207 1.94336C15.0586 3.92578 17.4121 7.92969 17.4121 12.7344ZM12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.85f)
        }
        return _sFMoonphaseWaxingCrescentInverse!!
    }

private var _sFMoonphaseWaxingCrescentInverse: ImageVector? = null
