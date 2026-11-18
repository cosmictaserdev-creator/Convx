package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMoonphaseWaningGibbousInverse (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFMoonphaseWaningGibbousInverse: ImageVector
    get() {
        if (_sFMoonphaseWaningGibbousInverse != null) {
            return _sFMoonphaseWaningGibbousInverse!!
        }
        _sFMoonphaseWaningGibbousInverse = sfIcon(
            name = "Monochrome.SFMoonphaseWaningGibbousInverse",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M23.6133 12.7246C23.623 18.7891 18.7793 23.623 12.7148 23.623C12.2754 23.623 11.8359 23.5938 11.3965 23.5254C15.1562 21.4746 17.3633 17.5586 17.3633 12.7344C17.3633 7.87109 15.1367 3.95508 11.3379 1.92383C11.7871 1.85547 12.2461 1.82617 12.7051 1.82617C18.7695 1.82617 23.6035 6.66016 23.6133 12.7246ZM12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.85f)
        }
        return _sFMoonphaseWaningGibbousInverse!!
    }

private var _sFMoonphaseWaningGibbousInverse: ImageVector? = null
