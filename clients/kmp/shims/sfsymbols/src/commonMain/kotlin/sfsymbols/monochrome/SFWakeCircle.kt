package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFWakeCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFWakeCircle: ImageVector
    get() {
        if (_sFWakeCircle != null) {
            return _sFWakeCircle!!
        }
        _sFWakeCircle = sfIcon(
            name = "Monochrome.SFWakeCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M12.7148 5.00977C8.45703 5.00977 5.00977 8.4668 5.00977 12.7246C5.00977 16.9727 8.45703 20.4297 12.7148 20.4297C16.9727 20.4297 20.4297 16.9727 20.4297 12.7246C20.4297 8.4668 16.9727 5.00977 12.7148 5.00977ZM6.57227 12.7246C6.57227 11.9336 6.71875 11.1816 7.00195 10.498L18.457 10.498C18.7305 11.1816 18.8867 11.9336 18.8867 12.7246C18.8867 16.123 16.123 18.877 12.7246 18.877C9.32617 18.877 6.57227 16.123 6.57227 12.7246ZM7.80273 9.05273C8.91602 7.54883 10.7031 6.5625 12.7246 6.5625C14.7461 6.5625 16.543 7.54883 17.6465 9.05273Z", fillAlpha = 0.85f)
        }
        return _sFWakeCircle!!
    }

private var _sFWakeCircle: ImageVector? = null
