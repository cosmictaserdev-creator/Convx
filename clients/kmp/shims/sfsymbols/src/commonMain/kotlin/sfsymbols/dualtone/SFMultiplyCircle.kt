package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMultiplyCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFMultiplyCircle: ImageVector
    get() {
        if (_sFMultiplyCircle != null) {
            return _sFMultiplyCircle!!
        }
        _sFMultiplyCircle = sfIcon(
            name = "Dualtone.SFMultiplyCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M8.98438 17.6953L17.6758 8.99414C17.8516 8.82812 17.9492 8.61328 17.9492 8.37891C17.9492 7.89062 17.5586 7.51953 17.0703 7.51953C16.8359 7.51953 16.6309 7.60742 16.4648 7.7832L7.74414 16.4746C7.56836 16.6504 7.48047 16.8457 7.48047 17.0996C7.48047 17.5781 7.86133 17.9688 8.34961 17.9688C8.60352 17.9688 8.80859 17.8711 8.98438 17.6953ZM16.4453 17.6953C16.6113 17.8711 16.8164 17.9688 17.0703 17.9688C17.5586 17.9688 17.9492 17.5781 17.9492 17.0996C17.9492 16.8457 17.8516 16.6504 17.6758 16.4746L8.96484 7.7832C8.78906 7.60742 8.59375 7.51953 8.34961 7.51953C7.86133 7.51953 7.48047 7.89062 7.48047 8.37891C7.48047 8.61328 7.56836 8.82812 7.74414 8.99414Z", fillAlpha = 0.85f)
        }
        return _sFMultiplyCircle!!
    }

private var _sFMultiplyCircle: ImageVector? = null
