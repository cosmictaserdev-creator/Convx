package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFInfoCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFInfoCircle: ImageVector
    get() {
        if (_sFInfoCircle != null) {
            return _sFInfoCircle!!
        }
        _sFInfoCircle = sfIcon(
            name = "Monochrome.SFInfoCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M10.4785 19.7168L15.625 19.7168C16.0742 19.7168 16.4258 19.3848 16.4258 18.9453C16.4258 18.5156 16.0742 18.1738 15.625 18.1738L13.9062 18.1738L13.9062 11.3477C13.9062 10.7812 13.6133 10.3906 13.0664 10.3906L10.6641 10.3906C10.2148 10.3906 9.86328 10.7227 9.86328 11.1523C9.86328 11.5918 10.2148 11.9238 10.6641 11.9238L12.1973 11.9238L12.1973 18.1738L10.4785 18.1738C10.0293 18.1738 9.6875 18.5156 9.6875 18.9453C9.6875 19.3848 10.0293 19.7168 10.4785 19.7168ZM12.627 8.16406C13.457 8.16406 14.1016 7.5 14.1016 6.68945C14.1016 5.85938 13.457 5.19531 12.627 5.19531C11.8066 5.19531 11.1523 5.85938 11.1523 6.68945C11.1523 7.5 11.8066 8.16406 12.627 8.16406Z", fillAlpha = 0.85f)
        }
        return _sFInfoCircle!!
    }

private var _sFInfoCircle: ImageVector? = null
