package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPillCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFPillCircle: ImageVector
    get() {
        if (_sFPillCircle != null) {
            return _sFPillCircle!!
        }
        _sFPillCircle = sfIcon(
            name = "Dualtone.SFPillCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M18.1934 7.24609C16.7188 5.81055 14.8242 5.81055 13.3398 7.30469L10.6445 9.99023L15.4395 14.7852L18.1348 12.0898C19.6289 10.5957 19.6484 8.66211 18.1934 7.24609ZM7.22656 18.2129C8.68164 19.6289 10.6055 19.6191 12.0801 18.1348L14.7656 15.4395L9.99023 10.6641L7.29492 13.3594C5.81055 14.8633 5.80078 16.7773 7.22656 18.2129Z", fillAlpha = 0.85f)
        }
        return _sFPillCircle!!
    }

private var _sFPillCircle: ImageVector? = null
