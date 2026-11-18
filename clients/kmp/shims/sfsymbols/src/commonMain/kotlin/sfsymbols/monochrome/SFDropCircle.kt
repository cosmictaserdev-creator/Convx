package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDropCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFDropCircle: ImageVector
    get() {
        if (_sFDropCircle != null) {
            return _sFDropCircle!!
        }
        _sFDropCircle = sfIcon(
            name = "Monochrome.SFDropCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M12.7051 18.9941C15.3027 18.9941 17.0508 17.3145 17.0508 14.7852C17.0508 13.5742 16.5527 12.4219 16.1035 11.4453C15.4102 9.95117 14.3359 8.14453 13.3398 6.64062C13.1641 6.35742 12.959 6.24023 12.7051 6.24023C12.4512 6.24023 12.2559 6.35742 12.0703 6.64062C11.084 8.14453 10 9.95117 9.30664 11.4453C8.86719 12.4219 8.35938 13.5742 8.35938 14.7852C8.35938 17.3145 10.1074 18.9941 12.7051 18.9941Z", fillAlpha = 0.85f)
        }
        return _sFDropCircle!!
    }

private var _sFDropCircle: ImageVector? = null
