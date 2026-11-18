package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMoonphaseWaxingGibbous (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFMoonphaseWaxingGibbous: ImageVector
    get() {
        if (_sFMoonphaseWaxingGibbous != null) {
            return _sFMoonphaseWaxingGibbous!!
        }
        _sFMoonphaseWaxingGibbous = sfIcon(
            name = "Dualtone.SFMoonphaseWaxingGibbous",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M8.02734 12.7344C8.02734 7.92969 10.3809 3.92578 14.209 1.94336C19.5312 2.65625 23.6035 7.19727 23.6133 12.7246C23.623 18.2812 19.4922 22.8418 14.1309 23.5156C10.3516 21.5527 8.02734 17.5684 8.02734 12.7344ZM12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.85f)
        }
        return _sFMoonphaseWaxingGibbous!!
    }

private var _sFMoonphaseWaxingGibbous: ImageVector? = null
