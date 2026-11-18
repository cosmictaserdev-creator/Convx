package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBookmarkCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFBookmarkCircle: ImageVector
    get() {
        if (_sFBookmarkCircle != null) {
            return _sFBookmarkCircle!!
        }
        _sFBookmarkCircle = sfIcon(
            name = "Dualtone.SFBookmarkCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M8.94531 19.3945C9.22852 19.3945 9.4043 19.2383 9.89258 18.7598L12.6465 16.0059C12.6855 15.9668 12.7344 15.9668 12.7637 16.0059L15.5273 18.7598C16.0059 19.2383 16.1816 19.3945 16.4746 19.3945C16.8555 19.3945 17.0996 19.1211 17.0996 18.6621L17.0996 7.99805C17.0996 6.79688 16.4844 6.17188 15.293 6.17188L10.1172 6.17188C8.92578 6.17188 8.31055 6.79688 8.31055 7.99805L8.31055 18.6621C8.31055 19.1211 8.55469 19.3945 8.94531 19.3945Z", fillAlpha = 0.85f)
        }
        return _sFBookmarkCircle!!
    }

private var _sFBookmarkCircle: ImageVector? = null
