package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBurn (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFBurn: ImageVector
    get() {
        if (_sFBurn != null) {
            return _sFBurn!!
        }
        _sFBurn = sfIcon(
            name = "Monochrome.SFBurn",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M0 12.7246C0 19.6777 5.76172 25.4395 12.7148 25.4395C19.6777 25.4395 25.4395 19.6777 25.4395 12.7246C25.4395 5.76172 19.668 0 12.7051 0C5.75195 0 0 5.76172 0 12.7246ZM1.82617 12.7344L12.6953 12.7344L7.25586 3.27148C8.84766 2.34375 10.7227 1.82617 12.7051 1.82617C14.6973 1.82617 16.5625 2.34375 18.1543 3.27148L12.6953 12.7344L23.6133 12.7344C23.623 16.7773 21.4355 20.3027 18.1641 22.168L12.6953 12.7344L7.25586 22.168C4.01367 20.2832 1.83594 16.7676 1.82617 12.7344Z", fillAlpha = 0.85f)
        }
        return _sFBurn!!
    }

private var _sFBurn: ImageVector? = null
