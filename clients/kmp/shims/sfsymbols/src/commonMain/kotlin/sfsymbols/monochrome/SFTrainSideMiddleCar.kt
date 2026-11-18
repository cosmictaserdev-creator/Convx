package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTrainSideMiddleCar (monochrome)
 * Viewport: 31.3672 x 19.7363
 */
public val SfSymbols.Monochrome.SFTrainSideMiddleCar: ImageVector
    get() {
        if (_sFTrainSideMiddleCar != null) {
            return _sFTrainSideMiddleCar!!
        }
        _sFTrainSideMiddleCar = sfIcon(
            name = "Monochrome.SFTrainSideMiddleCar",
            viewportWidth = 31.3672f,
            viewportHeight = 19.7363f
        ) {
            addSfPath("M0 16.0645C0 18.4961 1.24023 19.7363 3.71094 19.7363L27.2949 19.7363C29.7656 19.7363 31.0059 18.4961 31.0059 16.0645L31.0059 3.67188C31.0059 1.24023 29.7656 0 27.2949 0L3.71094 0C1.24023 0 0 1.24023 0 3.67188ZM4.21875 9.75586L4.21875 5.52734C4.21875 4.79492 4.64844 4.375 5.38086 4.375L25.6348 4.375C26.3574 4.375 26.7871 4.79492 26.7871 5.52734L26.7871 9.75586C26.7871 10.4492 26.3574 10.8789 25.6348 10.8789L5.38086 10.8789C4.64844 10.8789 4.21875 10.4492 4.21875 9.75586Z", fillAlpha = 0.85f)
        }
        return _sFTrainSideMiddleCar!!
    }

private var _sFTrainSideMiddleCar: ImageVector? = null
