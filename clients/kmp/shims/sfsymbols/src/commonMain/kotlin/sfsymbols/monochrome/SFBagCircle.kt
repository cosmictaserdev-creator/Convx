package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBagCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFBagCircle: ImageVector
    get() {
        if (_sFBagCircle != null) {
            return _sFBagCircle!!
        }
        _sFBagCircle = sfIcon(
            name = "Monochrome.SFBagCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M8.92578 18.8379L16.6699 18.8379C17.8418 18.8379 18.5156 18.1641 18.5156 16.875L18.5156 9.96094C18.5156 8.67188 17.832 8.00781 16.5137 8.00781L15.3711 8.00781C15.3516 6.66992 14.2188 5.50781 12.7148 5.50781C11.2207 5.50781 10.0879 6.66992 10.0586 8.00781L8.92578 8.00781C7.59766 8.00781 6.91406 8.66211 6.91406 9.96094L6.91406 16.875C6.91406 18.1738 7.59766 18.8379 8.92578 18.8379ZM11.1328 8.00781C11.1621 7.08984 11.7578 6.52344 12.7148 6.52344C13.6719 6.52344 14.2676 7.08984 14.2969 8.00781Z", fillAlpha = 0.85f)
        }
        return _sFBagCircle!!
    }

private var _sFBagCircle: ImageVector? = null
