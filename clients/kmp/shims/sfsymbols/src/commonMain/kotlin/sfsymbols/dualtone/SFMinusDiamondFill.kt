package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMinusDiamondFill (dualtone)
 * Viewport: 28.6086 x 28.2642
 */
public val SfSymbols.Dualtone.SFMinusDiamondFill: ImageVector
    get() {
        if (_sFMinusDiamondFill != null) {
            return _sFMinusDiamondFill!!
        }
        _sFMinusDiamondFill = sfIcon(
            name = "Dualtone.SFMinusDiamondFill",
            viewportWidth = 28.6086f,
            viewportHeight = 28.2642f
        ) {
            addSfPath("M1.35995 16.8128L11.4478 26.9007C13.235 28.6878 15.0318 28.6975 16.7994 26.93L26.9264 16.7932C28.6939 15.0257 28.6939 13.2288 26.9068 11.4417L16.8189 1.35378C15.0318-0.443093 13.2447-0.452858 11.4674 1.33425L1.32089 11.4612C-0.456453 13.2385-0.436922 15.0257 1.35995 16.8128Z", fillAlpha = 0.2125f)
            addSfPath("M8.85019 15.0745C8.23495 15.0745 7.7955 14.7327 7.7955 14.1565C7.7955 13.5608 8.21542 13.2093 8.85019 13.2093L19.4068 13.2093C20.0514 13.2093 20.4518 13.5608 20.4518 14.1565C20.4518 14.7327 20.0221 15.0745 19.4068 15.0745Z", fillAlpha = 0.85f)
        }
        return _sFMinusDiamondFill!!
    }

private var _sFMinusDiamondFill: ImageVector? = null
