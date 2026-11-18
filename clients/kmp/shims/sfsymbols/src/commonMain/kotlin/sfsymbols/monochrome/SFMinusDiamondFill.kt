package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMinusDiamondFill (monochrome)
 * Viewport: 28.6086 x 28.2642
 */
public val SfSymbols.Monochrome.SFMinusDiamondFill: ImageVector
    get() {
        if (_sFMinusDiamondFill != null) {
            return _sFMinusDiamondFill!!
        }
        _sFMinusDiamondFill = sfIcon(
            name = "Monochrome.SFMinusDiamondFill",
            viewportWidth = 28.6086f,
            viewportHeight = 28.2642f
        ) {
            addSfPath("M16.8189 1.35378L26.9068 11.4417C28.6939 13.2288 28.6939 15.0257 26.9264 16.7932L16.7994 26.93C15.0318 28.6975 13.235 28.6878 11.4478 26.9007L1.35995 16.8128C-0.436922 15.0257-0.456453 13.2385 1.32089 11.4612L11.4674 1.33425C13.2447-0.452858 15.0318-0.443093 16.8189 1.35378ZM8.85019 13.2093C8.21542 13.2093 7.7955 13.5608 7.7955 14.1565C7.7955 14.7327 8.23495 15.0745 8.85019 15.0745L19.4068 15.0745C20.0221 15.0745 20.4518 14.7327 20.4518 14.1565C20.4518 13.5608 20.0514 13.2093 19.4068 13.2093Z", fillAlpha = 0.85f)
        }
        return _sFMinusDiamondFill!!
    }

private var _sFMinusDiamondFill: ImageVector? = null
