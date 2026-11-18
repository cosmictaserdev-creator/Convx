package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFInsetFilledCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFInsetFilledCircle: ImageVector
    get() {
        if (_sFInsetFilledCircle != null) {
            return _sFInsetFilledCircle!!
        }
        _sFInsetFilledCircle = sfIcon(
            name = "Monochrome.SFInsetFilledCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M12.7051 21.9043C17.7832 21.9043 21.8945 17.793 21.8945 12.7246C21.8945 7.64648 17.7832 3.53516 12.7051 3.53516C7.63672 3.53516 3.51562 7.64648 3.51562 12.7246C3.51562 17.793 7.63672 21.9043 12.7051 21.9043Z", fillAlpha = 0.85f)
        }
        return _sFInsetFilledCircle!!
    }

private var _sFInsetFilledCircle: ImageVector? = null
