package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFGaugeWithNeedle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFGaugeWithNeedle: ImageVector
    get() {
        if (_sFGaugeWithNeedle != null) {
            return _sFGaugeWithNeedle!!
        }
        _sFGaugeWithNeedle = sfIcon(
            name = "Dualtone.SFGaugeWithNeedle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M12.7148 14.6484C13.7891 14.6484 14.6484 13.7891 14.6484 12.7246C14.6484 12.0703 14.3262 11.5039 13.8184 11.1426L7.5 6.63086C6.93359 6.23047 6.34766 6.80664 6.74805 7.39258L11.0352 13.6816C11.4258 14.2578 12.0117 14.6484 12.7148 14.6484Z", fillAlpha = 0.85f)
        }
        return _sFGaugeWithNeedle!!
    }

private var _sFGaugeWithNeedle: ImageVector? = null
