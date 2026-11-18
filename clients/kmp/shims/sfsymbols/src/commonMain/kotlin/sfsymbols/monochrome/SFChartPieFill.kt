package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFChartPieFill (monochrome)
 * Viewport: 25.8008 x 25.4395
 */
public val SfSymbols.Monochrome.SFChartPieFill: ImageVector
    get() {
        if (_sFChartPieFill != null) {
            return _sFChartPieFill!!
        }
        _sFChartPieFill = sfIcon(
            name = "Monochrome.SFChartPieFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.4395f
        ) {
            addSfPath("M12.7148 25.4395C15.791 25.4395 18.7109 24.3066 21.0156 22.3145L12.0703 13.5449C11.8457 13.3203 11.7871 13.125 11.7871 12.7832L11.7871 0.0488281C5.24414 0.527344 0 6.07422 0 12.7246C0 19.6875 5.76172 25.4395 12.7148 25.4395ZM25.4395 12.7148C25.4395 10.8203 24.9707 8.85742 24.1699 7.25586L13.877 13.1543L22.0801 21.2695C24.1602 19.0332 25.4395 15.9277 25.4395 12.7148ZM13.2422 11.8164L23.4473 5.9668C21.25 2.40234 17.1875 0.146484 13.2422 0.0195312Z", fillAlpha = 0.85f)
        }
        return _sFChartPieFill!!
    }

private var _sFChartPieFill: ImageVector? = null
