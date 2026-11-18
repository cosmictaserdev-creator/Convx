package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFChartPie (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFChartPie: ImageVector
    get() {
        if (_sFChartPie != null) {
            return _sFChartPie!!
        }
        _sFChartPie = sfIcon(
            name = "Monochrome.SFChartPie",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M20.4102 21.4062L21.5039 20.3906L14.2188 13.1738L23.0762 8.0957L22.3438 6.83594L13.5938 11.8359L13.5938 1.44531L12.1289 1.44531L12.1289 12.8418C12.1289 13.1348 12.1973 13.3496 12.4219 13.5645ZM12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
        }
        return _sFChartPie!!
    }

private var _sFChartPie: ImageVector? = null
