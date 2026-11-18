package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMinusCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFMinusCircle: ImageVector
    get() {
        if (_sFMinusCircle != null) {
            return _sFMinusCircle!!
        }
        _sFMinusCircle = sfIcon(
            name = "Dualtone.SFMinusCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M7.54883 13.6426L17.8711 13.6426C18.4668 13.6426 18.8867 13.3105 18.8867 12.7539C18.8867 12.1777 18.4961 11.8359 17.8711 11.8359L7.54883 11.8359C6.93359 11.8359 6.5332 12.1777 6.5332 12.7539C6.5332 13.3105 6.95312 13.6426 7.54883 13.6426Z", fillAlpha = 0.85f)
        }
        return _sFMinusCircle!!
    }

private var _sFMinusCircle: ImageVector? = null
