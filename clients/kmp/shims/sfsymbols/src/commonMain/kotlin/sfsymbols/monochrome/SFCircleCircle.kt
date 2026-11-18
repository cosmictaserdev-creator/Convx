package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCircleCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFCircleCircle: ImageVector
    get() {
        if (_sFCircleCircle != null) {
            return _sFCircleCircle!!
        }
        _sFCircleCircle = sfIcon(
            name = "Monochrome.SFCircleCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M12.7051 19.4531C16.4258 19.4531 19.4336 16.4453 19.4336 12.7246C19.4336 8.99414 16.4258 5.99609 12.7051 5.99609C8.98438 5.99609 5.97656 8.99414 5.97656 12.7246C5.97656 16.4453 8.98438 19.4531 12.7051 19.4531ZM12.7051 17.7734C9.92188 17.7734 7.65625 15.5078 7.65625 12.7246C7.65625 9.93164 9.92188 7.66602 12.7051 7.66602C15.498 7.66602 17.7539 9.93164 17.7539 12.7246C17.7539 15.5078 15.498 17.7734 12.7051 17.7734Z", fillAlpha = 0.85f)
        }
        return _sFCircleCircle!!
    }

private var _sFCircleCircle: ImageVector? = null
