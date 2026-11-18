package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPeacesign (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFPeacesign: ImageVector
    get() {
        if (_sFPeacesign != null) {
            return _sFPeacesign!!
        }
        _sFPeacesign = sfIcon(
            name = "Dualtone.SFPeacesign",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M3.33008 19.9902L4.56055 21.2207L11.8652 14.6094L11.8652 24.4922L13.5938 24.4922L13.5938 14.6484L20.8594 21.2207L22.0898 19.9902L13.5938 12.2168L13.5938 0.966797L11.8652 0.966797L11.8652 12.1777ZM12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
        }
        return _sFPeacesign!!
    }

private var _sFPeacesign: ImageVector? = null
