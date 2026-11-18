package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMoonCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFMoonCircle: ImageVector
    get() {
        if (_sFMoonCircle != null) {
            return _sFMoonCircle!!
        }
        _sFMoonCircle = sfIcon(
            name = "Dualtone.SFMoonCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M12.8027 20.0781C15.7422 20.0781 18.1641 18.3008 19.248 15.957C19.4434 15.5664 19.1797 15.2734 18.7891 15.4004C18.3008 15.5762 17.4316 15.791 16.5723 15.791C12.4219 15.791 10.0781 13.4277 10.0781 9.28711C10.0781 8.44727 10.2539 7.61719 10.5273 6.93359C10.6934 6.48438 10.4102 6.23047 10 6.40625C7.69531 7.37305 5.67383 9.81445 5.67383 12.959C5.67383 16.8848 8.86719 20.0781 12.8027 20.0781Z", fillAlpha = 0.85f)
        }
        return _sFMoonCircle!!
    }

private var _sFMoonCircle: ImageVector? = null
