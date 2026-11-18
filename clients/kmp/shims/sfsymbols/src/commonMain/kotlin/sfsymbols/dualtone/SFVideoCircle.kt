package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFVideoCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFVideoCircle: ImageVector
    get() {
        if (_sFVideoCircle != null) {
            return _sFVideoCircle!!
        }
        _sFVideoCircle = sfIcon(
            name = "Dualtone.SFVideoCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M7.49023 17.7637L14.1504 17.7637C15.4102 17.7637 16.1426 17.041 16.1426 15.791L16.1426 9.6875C16.1426 8.42773 15.459 7.71484 14.1992 7.71484L7.49023 7.71484C6.26953 7.71484 5.48828 8.42773 5.48828 9.6875L5.48828 15.791C5.48828 17.041 6.23047 17.7637 7.49023 17.7637ZM16.8359 14.4629L19.2285 16.4844C19.4531 16.6602 19.6875 16.7871 19.9121 16.7871C20.3613 16.7871 20.6641 16.4551 20.6641 15.9473L20.6641 9.51172C20.6641 9.02344 20.3613 8.69141 19.9121 8.69141C19.6875 8.69141 19.4434 8.81836 19.2285 8.98438L16.8359 11.0059Z", fillAlpha = 0.85f)
        }
        return _sFVideoCircle!!
    }

private var _sFVideoCircle: ImageVector? = null
