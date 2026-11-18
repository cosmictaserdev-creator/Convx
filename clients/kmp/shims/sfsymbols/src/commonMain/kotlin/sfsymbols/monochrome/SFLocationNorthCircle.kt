package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLocationNorthCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFLocationNorthCircle: ImageVector
    get() {
        if (_sFLocationNorthCircle != null) {
            return _sFLocationNorthCircle!!
        }
        _sFLocationNorthCircle = sfIcon(
            name = "Monochrome.SFLocationNorthCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M7.08984 18.2422C6.79688 18.9746 7.59766 19.5898 8.26172 18.916L12.4121 14.7656C12.5879 14.5898 12.8125 14.5898 12.998 14.7656L17.1387 18.916C17.8125 19.5898 18.6035 18.9746 18.3203 18.2422L13.4766 5.83984C13.1836 5.08789 12.2168 5.09766 11.9141 5.85938Z", fillAlpha = 0.85f)
        }
        return _sFLocationNorthCircle!!
    }

private var _sFLocationNorthCircle: ImageVector? = null
