package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFMCircle: ImageVector
    get() {
        if (_sFMCircle != null) {
            return _sFMCircle!!
        }
        _sFMCircle = sfIcon(
            name = "Dualtone.SFMCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M7.8418 18.5254C8.33984 18.5254 8.63281 18.2031 8.63281 17.6465L8.63281 9.80469L8.73047 9.80469L11.8359 17.6172C12.002 18.0176 12.2754 18.2031 12.6953 18.2031C13.1152 18.2031 13.3984 18.0176 13.5645 17.6172L16.6797 9.80469L16.7773 9.80469L16.7773 17.6465C16.7773 18.1934 17.0703 18.5254 17.5488 18.5254C18.0469 18.5254 18.3496 18.2031 18.3496 17.6465L18.3496 7.82227C18.3496 7.2168 17.9102 6.80664 17.3047 6.80664C16.7676 6.80664 16.4062 7.04102 16.2109 7.53906L12.7441 16.25L12.6758 16.25L9.19922 7.53906C8.99414 7.04102 8.64258 6.80664 8.10547 6.80664C7.49023 6.80664 7.06055 7.2168 7.06055 7.82227L7.06055 17.6465C7.06055 18.1934 7.35352 18.5254 7.8418 18.5254Z", fillAlpha = 0.85f)
        }
        return _sFMCircle!!
    }

private var _sFMCircle: ImageVector? = null
