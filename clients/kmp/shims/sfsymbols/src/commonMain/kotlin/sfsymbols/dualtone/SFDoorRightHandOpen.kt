package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDoorRightHandOpen (dualtone)
 * Viewport: 18.9453 x 26.4551
 */
public val SfSymbols.Dualtone.SFDoorRightHandOpen: ImageVector
    get() {
        if (_sFDoorRightHandOpen != null) {
            return _sFDoorRightHandOpen!!
        }
        _sFDoorRightHandOpen = sfIcon(
            name = "Dualtone.SFDoorRightHandOpen",
            viewportWidth = 18.9453f,
            viewportHeight = 26.4551f
        ) {
            addSfPath("M18.584 25.5664L18.584 2.28516C18.584 0.917969 17.6465 0 16.2402 0L2.34375 0C0.9375 0 0 0.917969 0 2.28516L0 25.5664C0 26.0449 0.380859 26.4355 0.869141 26.4355C1.34766 26.4355 1.73828 26.0449 1.73828 25.5664L1.73828 2.40234C1.73828 2.00195 1.99219 1.73828 2.37305 1.73828L16.2109 1.73828C16.5918 1.73828 16.8457 2.00195 16.8457 2.40234L16.8457 25.5664C16.8457 26.0449 17.2266 26.4355 17.7148 26.4355C18.1934 26.4355 18.584 26.0449 18.584 25.5664Z", fillAlpha = 0.425f)
            addSfPath("M15.4883 25.7617L15.4883 3.33984C15.4883 3.125 15.3027 3.00781 15.0879 3.10547L10.9473 4.98047C10.6738 5.09766 10.5762 5.19531 10.5762 5.47852L10.5762 23.623C10.5762 23.9062 10.6738 24.0137 10.9766 24.1406L15.0879 26.0059C15.3027 26.0938 15.4883 25.9863 15.4883 25.7617Z", fillAlpha = 0.85f)
        }
        return _sFDoorRightHandOpen!!
    }

private var _sFDoorRightHandOpen: ImageVector? = null
