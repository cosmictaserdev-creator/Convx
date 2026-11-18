package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFChevronForwardCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFChevronForwardCircle: ImageVector
    get() {
        if (_sFChevronForwardCircle != null) {
            return _sFChevronForwardCircle!!
        }
        _sFChevronForwardCircle = sfIcon(
            name = "Monochrome.SFChevronForwardCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M9.89258 18.9258C10.1855 19.209 10.7422 19.1992 11.0547 18.8965L16.4258 13.7793C17.041 13.2129 17.041 12.2461 16.4258 11.6699L11.0547 6.55273C10.7129 6.23047 10.2148 6.2207 9.90234 6.52344C9.57031 6.83594 9.56055 7.37305 9.89258 7.67578L15.1953 12.7148L9.89258 17.7637C9.57031 18.0762 9.57031 18.6035 9.89258 18.9258Z", fillAlpha = 0.85f)
        }
        return _sFChevronForwardCircle!!
    }

private var _sFChevronForwardCircle: ImageVector? = null
