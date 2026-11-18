package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSquareFilledOnSquare (dualtone)
 * Viewport: 26.7285 x 25.9082
 */
public val SfSymbols.Dualtone.SFSquareFilledOnSquare: ImageVector
    get() {
        if (_sFSquareFilledOnSquare != null) {
            return _sFSquareFilledOnSquare!!
        }
        _sFSquareFilledOnSquare = sfIcon(
            name = "Dualtone.SFSquareFilledOnSquare",
            viewportWidth = 26.7285f,
            viewportHeight = 25.9082f
        ) {
            addSfPath("M20.2637 3.78906L20.2637 5.64453L18.5254 5.64453L18.5254 3.88672C18.5254 2.48047 17.7637 1.75781 16.4258 1.75781L3.83789 1.75781C2.4707 1.75781 1.72852 2.48047 1.72852 3.88672L1.72852 16.4355C1.72852 17.8418 2.4707 18.5645 3.83789 18.5645L6.11328 18.5645L6.11328 20.293L3.79883 20.293C1.26953 20.293 0 19.0234 0 16.5234L0 3.78906C0 1.29883 1.26953 0.0292969 3.79883 0.0292969L16.4551 0.0292969C18.9746 0.0292969 20.2637 1.30859 20.2637 3.78906Z", fillAlpha = 0.425f)
            addSfPath("M9.91211 25.9082L22.5684 25.9082C25.0879 25.9082 26.3672 24.6289 26.3672 22.1387L26.3672 9.41406C26.3672 6.92383 25.0879 5.64453 22.5684 5.64453L9.91211 5.64453C7.38281 5.64453 6.11328 6.91406 6.11328 9.41406L6.11328 22.1387C6.11328 24.6387 7.38281 25.9082 9.91211 25.9082Z", fillAlpha = 0.85f)
        }
        return _sFSquareFilledOnSquare!!
    }

private var _sFSquareFilledOnSquare: ImageVector? = null
