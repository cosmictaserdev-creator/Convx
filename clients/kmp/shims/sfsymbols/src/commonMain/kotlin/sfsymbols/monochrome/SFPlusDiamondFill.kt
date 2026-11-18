package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPlusDiamondFill (monochrome)
 * Viewport: 28.6086 x 28.2642
 */
public val SfSymbols.Monochrome.SFPlusDiamondFill: ImageVector
    get() {
        if (_sFPlusDiamondFill != null) {
            return _sFPlusDiamondFill!!
        }
        _sFPlusDiamondFill = sfIcon(
            name = "Monochrome.SFPlusDiamondFill",
            viewportWidth = 28.6086f,
            viewportHeight = 28.2642f
        ) {
            addSfPath("M16.8189 1.35378L26.9068 11.4417C28.6939 13.2288 28.6939 15.0257 26.9264 16.7932L16.7994 26.93C15.0318 28.6975 13.235 28.6878 11.4478 26.9007L1.35995 16.8128C-0.436922 15.0257-0.456453 13.2385 1.32089 11.4612L11.4674 1.33425C13.2447-0.452858 15.0318-0.443093 16.8189 1.35378ZM13.1959 9.14675L13.1959 13.18L9.16269 13.18C8.59628 13.18 8.20566 13.5608 8.20566 14.1272C8.20566 14.6643 8.60605 15.0354 9.16269 15.0354L13.1959 15.0354L13.1959 19.0784C13.1959 19.6253 13.567 20.0257 14.1041 20.0257C14.6705 20.0257 15.0514 19.635 15.0514 19.0784L15.0514 15.0354L19.0943 15.0354C19.6412 15.0354 20.0416 14.6643 20.0416 14.1272C20.0416 13.5608 19.651 13.18 19.0943 13.18L15.0514 13.18L15.0514 9.14675C15.0514 8.58034 14.6705 8.18972 14.1041 8.18972C13.567 8.18972 13.1959 8.59011 13.1959 9.14675Z", fillAlpha = 0.85f)
        }
        return _sFPlusDiamondFill!!
    }

private var _sFPlusDiamondFill: ImageVector? = null
