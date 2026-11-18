package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFExclamationmarkCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFExclamationmarkCircle: ImageVector
    get() {
        if (_sFExclamationmarkCircle != null) {
            return _sFExclamationmarkCircle!!
        }
        _sFExclamationmarkCircle = sfIcon(
            name = "Monochrome.SFExclamationmarkCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M12.6953 14.9902C13.2227 14.9902 13.5352 14.6777 13.5449 14.1016L13.7012 7.39258C13.7109 6.82617 13.2715 6.40625 12.6855 6.40625C12.0898 6.40625 11.6797 6.81641 11.6895 7.38281L11.8359 14.1016C11.8457 14.668 12.1582 14.9902 12.6953 14.9902ZM12.6953 18.9648C13.3691 18.9648 13.9355 18.4277 13.9355 17.7637C13.9355 17.0898 13.3789 16.5625 12.6953 16.5625C12.0215 16.5625 11.4648 17.0996 11.4648 17.7637C11.4648 18.418 12.0312 18.9648 12.6953 18.9648Z", fillAlpha = 0.85f)
        }
        return _sFExclamationmarkCircle!!
    }

private var _sFExclamationmarkCircle: ImageVector? = null
