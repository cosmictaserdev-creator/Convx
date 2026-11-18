package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFICircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFICircle: ImageVector
    get() {
        if (_sFICircle != null) {
            return _sFICircle!!
        }
        _sFICircle = sfIcon(
            name = "Dualtone.SFICircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M12.6953 18.4668C13.2617 18.4668 13.5645 18.0957 13.5645 17.5098L13.5645 7.76367C13.5645 7.16797 13.2617 6.80664 12.6953 6.80664C12.1484 6.80664 11.8457 7.1875 11.8457 7.76367L11.8457 17.5098C11.8457 18.0762 12.1484 18.4668 12.6953 18.4668Z", fillAlpha = 0.85f)
        }
        return _sFICircle!!
    }

private var _sFICircle: ImageVector? = null
