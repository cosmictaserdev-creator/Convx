package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFChevronDownCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFChevronDownCircle: ImageVector
    get() {
        if (_sFChevronDownCircle != null) {
            return _sFChevronDownCircle!!
        }
        _sFChevronDownCircle = sfIcon(
            name = "Dualtone.SFChevronDownCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M13.7793 16.8164L18.8965 11.4453C19.2188 11.0938 19.2383 10.5957 18.9258 10.2832C18.5938 9.95117 18.0664 9.93164 17.7539 10.2734L12.7148 15.5664L7.67578 10.2734C7.36328 9.93164 6.81641 9.94141 6.50391 10.2832C6.20117 10.6055 6.21094 11.0938 6.5332 11.4453L11.6504 16.8164C12.3242 17.5391 13.1055 17.5293 13.7793 16.8164Z", fillAlpha = 0.85f)
        }
        return _sFChevronDownCircle!!
    }

private var _sFChevronDownCircle: ImageVector? = null
