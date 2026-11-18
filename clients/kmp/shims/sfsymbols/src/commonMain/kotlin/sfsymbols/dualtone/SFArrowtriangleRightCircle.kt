package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowtriangleRightCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFArrowtriangleRightCircle: ImageVector
    get() {
        if (_sFArrowtriangleRightCircle != null) {
            return _sFArrowtriangleRightCircle!!
        }
        _sFArrowtriangleRightCircle = sfIcon(
            name = "Dualtone.SFArrowtriangleRightCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M10.332 17.6465L17.5195 13.3789C18.0273 13.0762 18.0176 12.3926 17.5195 12.0898L10.332 7.80273C9.82422 7.5 9.15039 7.74414 9.15039 8.31055L9.15039 17.1484C9.15039 17.7148 9.78516 17.9785 10.332 17.6465Z", fillAlpha = 0.85f)
        }
        return _sFArrowtriangleRightCircle!!
    }

private var _sFArrowtriangleRightCircle: ImageVector? = null
