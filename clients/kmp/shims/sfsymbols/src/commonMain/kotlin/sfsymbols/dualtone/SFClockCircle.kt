package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFClockCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFClockCircle: ImageVector
    get() {
        if (_sFClockCircle != null) {
            return _sFClockCircle!!
        }
        _sFClockCircle = sfIcon(
            name = "Dualtone.SFClockCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M9.16992 13.5449C8.84766 13.5449 8.62305 13.3203 8.62305 12.998C8.62305 12.6855 8.84766 12.4512 9.16992 12.4512L12.168 12.4512L12.168 8.4082C12.168 8.10547 12.4023 7.87109 12.7051 7.87109C13.0273 7.87109 13.2715 8.10547 13.2715 8.4082L13.2715 12.998C13.2715 13.3105 13.0078 13.5449 12.7051 13.5449ZM12.7148 19.4922C16.416 19.4922 19.4824 16.4258 19.4824 12.7246C19.4824 9.02344 16.416 5.95703 12.7051 5.95703C9.00391 5.95703 5.94727 9.02344 5.94727 12.7246C5.94727 16.4258 9.01367 19.4922 12.7148 19.4922Z", fillAlpha = 0.85f)
        }
        return _sFClockCircle!!
    }

private var _sFClockCircle: ImageVector? = null
