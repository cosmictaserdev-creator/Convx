package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFForwardEndCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFForwardEndCircle: ImageVector
    get() {
        if (_sFForwardEndCircle != null) {
            return _sFForwardEndCircle!!
        }
        _sFForwardEndCircle = sfIcon(
            name = "Dualtone.SFForwardEndCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M8.06641 17.3828C8.28125 17.3828 8.49609 17.3047 8.71094 17.1777L14.873 13.5449C15.2441 13.3398 15.4102 13.0566 15.4102 12.7441C15.4102 12.4316 15.2441 12.1582 14.873 11.9434L8.71094 8.32031C8.49609 8.19336 8.28125 8.11523 8.06641 8.11523C7.63672 8.11523 7.27539 8.4375 7.27539 9.05273L7.27539 16.4355C7.27539 17.0605 7.63672 17.3828 8.06641 17.3828ZM17.4023 17.5586C17.8906 17.5586 18.1445 17.3145 18.1445 16.8164L18.1445 8.63281C18.1445 8.13477 17.8906 7.90039 17.4023 7.90039L16.0547 7.90039C15.5664 7.90039 15.3223 8.125 15.3223 8.63281L15.3223 16.8164C15.3223 17.3145 15.5664 17.5586 16.0547 17.5586Z", fillAlpha = 0.85f)
        }
        return _sFForwardEndCircle!!
    }

private var _sFForwardEndCircle: ImageVector? = null
