package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSmallcircleFilledCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFSmallcircleFilledCircle: ImageVector
    get() {
        if (_sFSmallcircleFilledCircle != null) {
            return _sFSmallcircleFilledCircle!!
        }
        _sFSmallcircleFilledCircle = sfIcon(
            name = "Dualtone.SFSmallcircleFilledCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M12.7051 16.5625C14.8242 16.5625 16.543 14.8438 16.543 12.7246C16.543 10.6055 14.8242 8.87695 12.7051 8.87695C10.5859 8.87695 8.86719 10.6055 8.86719 12.7246C8.86719 14.8438 10.5859 16.5625 12.7051 16.5625Z", fillAlpha = 0.85f)
        }
        return _sFSmallcircleFilledCircle!!
    }

private var _sFSmallcircleFilledCircle: ImageVector? = null
