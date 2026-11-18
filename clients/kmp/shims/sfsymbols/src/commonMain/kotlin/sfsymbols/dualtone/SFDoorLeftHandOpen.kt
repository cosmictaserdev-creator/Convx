package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDoorLeftHandOpen (dualtone)
 * Viewport: 18.9453 x 26.4551
 */
public val SfSymbols.Dualtone.SFDoorLeftHandOpen: ImageVector
    get() {
        if (_sFDoorLeftHandOpen != null) {
            return _sFDoorLeftHandOpen!!
        }
        _sFDoorLeftHandOpen = sfIcon(
            name = "Dualtone.SFDoorLeftHandOpen",
            viewportWidth = 18.9453f,
            viewportHeight = 26.4551f
        ) {
            addSfPath("M0 25.5664C0 26.0449 0.380859 26.4355 0.869141 26.4355C1.34766 26.4355 1.73828 26.0449 1.73828 25.5664L1.73828 2.40234C1.73828 2.00195 1.99219 1.73828 2.37305 1.73828L16.2109 1.73828C16.5918 1.73828 16.8457 2.00195 16.8457 2.40234L16.8457 25.5664C16.8457 26.0449 17.2266 26.4355 17.7148 26.4355C18.1934 26.4355 18.584 26.0449 18.584 25.5664L18.584 2.28516C18.584 0.917969 17.6465 0 16.2402 0L2.34375 0C0.9375 0 0 0.917969 0 2.28516Z", fillAlpha = 0.425f)
            addSfPath("M3.0957 25.7617C3.0957 25.9863 3.28125 26.0938 3.49609 26.0059L7.60742 24.1406C7.91016 24.0137 8.00781 23.9062 8.00781 23.623L8.00781 5.47852C8.00781 5.19531 7.91016 5.09766 7.63672 4.98047L3.49609 3.10547C3.28125 3.00781 3.0957 3.125 3.0957 3.33984Z", fillAlpha = 0.85f)
        }
        return _sFDoorLeftHandOpen!!
    }

private var _sFDoorLeftHandOpen: ImageVector? = null
