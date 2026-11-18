package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBackwardEndCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFBackwardEndCircle: ImageVector
    get() {
        if (_sFBackwardEndCircle != null) {
            return _sFBackwardEndCircle!!
        }
        _sFBackwardEndCircle = sfIcon(
            name = "Dualtone.SFBackwardEndCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M17.3438 17.3828C17.7734 17.3828 18.1445 17.0605 18.1445 16.4355L18.1445 9.05273C18.1445 8.4375 17.7734 8.11523 17.3438 8.11523C17.1289 8.11523 16.9141 8.19336 16.709 8.32031L10.5371 11.9434C10.1758 12.1582 10 12.4316 10 12.7441C10 13.0566 10.1758 13.3398 10.5371 13.5449L16.709 17.1777C16.9141 17.3047 17.1289 17.3828 17.3438 17.3828ZM8.00781 17.5586L9.35547 17.5586C9.84375 17.5586 10.0977 17.3145 10.0977 16.8164L10.0977 8.63281C10.0977 8.125 9.84375 7.90039 9.35547 7.90039L8.00781 7.90039C7.5293 7.90039 7.27539 8.13477 7.27539 8.63281L7.27539 16.8164C7.27539 17.3145 7.5293 17.5586 8.00781 17.5586Z", fillAlpha = 0.85f)
        }
        return _sFBackwardEndCircle!!
    }

private var _sFBackwardEndCircle: ImageVector? = null
