package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPlusDiamondFill (dualtone)
 * Viewport: 28.6086 x 28.2642
 */
public val SfSymbols.Dualtone.SFPlusDiamondFill: ImageVector
    get() {
        if (_sFPlusDiamondFill != null) {
            return _sFPlusDiamondFill!!
        }
        _sFPlusDiamondFill = sfIcon(
            name = "Dualtone.SFPlusDiamondFill",
            viewportWidth = 28.6086f,
            viewportHeight = 28.2642f
        ) {
            addSfPath("M1.35995 16.8128L11.4478 26.9007C13.235 28.6878 15.0318 28.6975 16.7994 26.93L26.9264 16.7932C28.6939 15.0257 28.6939 13.2288 26.9068 11.4417L16.8189 1.35378C15.0318-0.443093 13.2447-0.452858 11.4674 1.33425L1.32089 11.4612C-0.456453 13.2385-0.436922 15.0257 1.35995 16.8128Z", fillAlpha = 0.2125f)
            addSfPath("M15.0514 19.0784L15.0514 9.14675C15.0514 8.58034 14.6705 8.18972 14.1041 8.18972C13.567 8.18972 13.1959 8.59011 13.1959 9.14675L13.1959 19.0784C13.1959 19.6253 13.567 20.0257 14.1041 20.0257C14.6705 20.0257 15.0514 19.635 15.0514 19.0784ZM9.16269 15.0354L19.0943 15.0354C19.6412 15.0354 20.0416 14.6643 20.0416 14.1272C20.0416 13.5608 19.651 13.18 19.0943 13.18L9.16269 13.18C8.59628 13.18 8.20566 13.5608 8.20566 14.1272C8.20566 14.6643 8.60605 15.0354 9.16269 15.0354Z", fillAlpha = 0.85f)
        }
        return _sFPlusDiamondFill!!
    }

private var _sFPlusDiamondFill: ImageVector? = null
