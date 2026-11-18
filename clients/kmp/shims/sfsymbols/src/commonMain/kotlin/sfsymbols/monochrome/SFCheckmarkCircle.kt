package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCheckmarkCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFCheckmarkCircle: ImageVector
    get() {
        if (_sFCheckmarkCircle != null) {
            return _sFCheckmarkCircle!!
        }
        _sFCheckmarkCircle = sfIcon(
            name = "Monochrome.SFCheckmarkCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M11.3379 18.7012C11.6992 18.7012 11.9922 18.5352 12.207 18.2031L18.1934 8.81836C18.3203 8.62305 18.4473 8.37891 18.4473 8.14453C18.4473 7.66602 18.0176 7.34375 17.5781 7.34375C17.2949 7.34375 17.0215 7.51953 16.8262 7.83203L11.2988 16.6113L8.54492 13.1641C8.29102 12.832 8.04688 12.7344 7.75391 12.7344C7.28516 12.7344 6.91406 13.1152 6.91406 13.5938C6.91406 13.8281 7.01172 14.0625 7.16797 14.2676L10.4297 18.2031C10.7031 18.5547 10.9766 18.7012 11.3379 18.7012Z", fillAlpha = 0.85f)
        }
        return _sFCheckmarkCircle!!
    }

private var _sFCheckmarkCircle: ImageVector? = null
