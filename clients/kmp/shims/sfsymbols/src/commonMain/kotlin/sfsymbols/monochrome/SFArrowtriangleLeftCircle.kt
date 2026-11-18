package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowtriangleLeftCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFArrowtriangleLeftCircle: ImageVector
    get() {
        if (_sFArrowtriangleLeftCircle != null) {
            return _sFArrowtriangleLeftCircle!!
        }
        _sFArrowtriangleLeftCircle = sfIcon(
            name = "Monochrome.SFArrowtriangleLeftCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M7.67578 13.3789L14.8633 17.6465C15.4102 17.9785 16.0449 17.7148 16.0449 17.1484L16.0449 8.31055C16.0449 7.74414 15.3711 7.49023 14.8633 7.80273L7.67578 12.0898C7.17773 12.3828 7.16797 13.0762 7.67578 13.3789Z", fillAlpha = 0.85f)
        }
        return _sFArrowtriangleLeftCircle!!
    }

private var _sFArrowtriangleLeftCircle: ImageVector? = null
