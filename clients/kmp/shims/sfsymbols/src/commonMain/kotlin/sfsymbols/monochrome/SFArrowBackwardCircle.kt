package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowBackwardCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFArrowBackwardCircle: ImageVector
    get() {
        if (_sFArrowBackwardCircle != null) {
            return _sFArrowBackwardCircle!!
        }
        _sFArrowBackwardCircle = sfIcon(
            name = "Monochrome.SFArrowBackwardCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M18.2129 13.5645C18.6621 13.5645 19.0527 13.1738 19.0527 12.7148C19.0527 12.2461 18.6621 11.8555 18.2129 11.8555L10.293 11.8652L8.30078 11.9336C7.87109 11.9531 7.51953 12.2852 7.51953 12.7148C7.51953 13.1348 7.87109 13.4766 8.30078 13.4961L10.293 13.5645ZM11.3574 17.7734C11.7871 17.7734 12.1094 17.4414 12.1094 17.002C12.1094 16.7871 12.0215 16.5527 11.8555 16.4062L9.67773 14.3262L7.98828 12.7148L7.98828 12.7148L9.67773 11.1035L11.8555 9.02344C12.0215 8.87695 12.1094 8.65234 12.1094 8.4375C12.1094 7.99805 11.7871 7.65625 11.3574 7.65625C11.123 7.65625 10.9473 7.74414 10.791 7.91016L6.71875 12.0898C6.50391 12.3047 6.43555 12.4902 6.43555 12.7148C6.43555 12.9297 6.50391 13.1152 6.71875 13.3301L10.791 17.5195C10.9473 17.6855 11.123 17.7734 11.3574 17.7734Z", fillAlpha = 0.85f)
        }
        return _sFArrowBackwardCircle!!
    }

private var _sFArrowBackwardCircle: ImageVector? = null
