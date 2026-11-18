package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSquareCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFSquareCircle: ImageVector
    get() {
        if (_sFSquareCircle != null) {
            return _sFSquareCircle!!
        }
        _sFSquareCircle = sfIcon(
            name = "Dualtone.SFSquareCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M8.07617 19.0625L17.3535 19.0625C18.4961 19.0625 19.043 18.4277 19.043 17.1875L19.043 8.06641C19.043 6.94336 18.4961 6.38672 17.3535 6.38672L8.07617 6.38672C7.06055 6.38672 6.38672 7.06055 6.38672 8.06641L6.38672 17.1875C6.38672 18.3008 7.06055 19.0625 8.07617 19.0625ZM8.69141 17.4805C8.26172 17.4805 7.96875 17.1777 7.96875 16.7383L7.96875 8.71094C7.96875 8.27148 8.26172 7.96875 8.69141 7.96875L16.7383 7.96875C17.168 7.96875 17.4609 8.27148 17.4609 8.71094L17.4609 16.7383C17.4609 17.1777 17.168 17.4805 16.7383 17.4805Z", fillAlpha = 0.85f)
        }
        return _sFSquareCircle!!
    }

private var _sFSquareCircle: ImageVector? = null
