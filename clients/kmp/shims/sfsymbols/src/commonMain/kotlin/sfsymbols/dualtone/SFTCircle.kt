package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFTCircle: ImageVector
    get() {
        if (_sFTCircle != null) {
            return _sFTCircle!!
        }
        _sFTCircle = sfIcon(
            name = "Dualtone.SFTCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M12.666 18.5059C13.2227 18.5059 13.5352 18.1348 13.5352 17.5488L13.5352 8.4082L16.7773 8.4082C17.1973 8.4082 17.5098 8.125 17.5098 7.68555C17.5098 7.23633 17.1973 6.97266 16.7773 6.97266L8.62305 6.97266C8.20312 6.97266 7.89062 7.23633 7.89062 7.68555C7.89062 8.125 8.20312 8.4082 8.62305 8.4082L11.8066 8.4082L11.8066 17.5488C11.8066 18.1152 12.1094 18.5059 12.666 18.5059Z", fillAlpha = 0.85f)
        }
        return _sFTCircle!!
    }

private var _sFTCircle: ImageVector? = null
