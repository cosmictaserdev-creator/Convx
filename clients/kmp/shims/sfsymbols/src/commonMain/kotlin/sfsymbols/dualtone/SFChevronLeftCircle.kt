package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFChevronLeftCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFChevronLeftCircle: ImageVector
    get() {
        if (_sFChevronLeftCircle != null) {
            return _sFChevronLeftCircle!!
        }
        _sFChevronLeftCircle = sfIcon(
            name = "Dualtone.SFChevronLeftCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M15.1562 18.9258C15.4883 18.6035 15.4785 18.0762 15.1562 17.7637L9.85352 12.7148L15.1562 7.67578C15.4883 7.37305 15.4785 6.83594 15.1465 6.52344C14.834 6.2207 14.3359 6.23047 14.0039 6.55273L8.62305 11.6699C8.00781 12.2461 8.01758 13.2129 8.62305 13.7793L14.0039 18.8965C14.3066 19.1992 14.873 19.209 15.1562 18.9258Z", fillAlpha = 0.85f)
        }
        return _sFChevronLeftCircle!!
    }

private var _sFChevronLeftCircle: ImageVector? = null
