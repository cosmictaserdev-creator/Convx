package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowtriangleUpCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFArrowtriangleUpCircle: ImageVector
    get() {
        if (_sFArrowtriangleUpCircle != null) {
            return _sFArrowtriangleUpCircle!!
        }
        _sFArrowtriangleUpCircle = sfIcon(
            name = "Dualtone.SFArrowtriangleUpCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M8.28125 16.5527L17.1289 16.5527C17.6953 16.5527 17.9492 15.9277 17.6172 15.3711L13.3594 8.18359C13.0469 7.66602 12.3633 7.68555 12.0605 8.18359L7.77344 15.3711C7.4707 15.8887 7.71484 16.5527 8.28125 16.5527Z", fillAlpha = 0.85f)
        }
        return _sFArrowtriangleUpCircle!!
    }

private var _sFArrowtriangleUpCircle: ImageVector? = null
