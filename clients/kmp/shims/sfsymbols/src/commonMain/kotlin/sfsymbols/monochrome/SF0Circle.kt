package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SF0Circle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SF0Circle: ImageVector
    get() {
        if (_sF0Circle != null) {
            return _sF0Circle!!
        }
        _sF0Circle = sfIcon(
            name = "Monochrome.SF0Circle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M12.7051 18.6621C15.293 18.6621 17.041 16.2695 17.041 12.7246C17.041 9.16992 15.293 6.78711 12.7051 6.78711C10.1172 6.78711 8.37891 9.16992 8.37891 12.7246C8.37891 16.2695 10.1172 18.6621 12.7051 18.6621ZM12.7051 17.2559C11.1133 17.2559 10.0391 15.4297 10.0391 12.7246C10.0391 10.0098 11.1133 8.19336 12.7051 8.19336C14.2969 8.19336 15.3711 10.0098 15.3711 12.7246C15.3711 15.4297 14.2969 17.2559 12.7051 17.2559Z", fillAlpha = 0.85f)
        }
        return _sF0Circle!!
    }

private var _sF0Circle: ImageVector? = null
