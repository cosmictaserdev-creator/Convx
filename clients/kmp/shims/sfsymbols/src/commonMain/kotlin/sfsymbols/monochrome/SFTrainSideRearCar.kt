package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTrainSideRearCar (monochrome)
 * Viewport: 31.3672 x 19.7363
 */
public val SfSymbols.Monochrome.SFTrainSideRearCar: ImageVector
    get() {
        if (_sFTrainSideRearCar != null) {
            return _sFTrainSideRearCar!!
        }
        _sFTrainSideRearCar = sfIcon(
            name = "Monochrome.SFTrainSideRearCar",
            viewportWidth = 31.3672f,
            viewportHeight = 19.7363f
        ) {
            addSfPath("M0 16.0645C0 18.4961 1.24023 19.7363 3.71094 19.7363L27.2949 19.7363C29.7656 19.7363 31.0059 18.4961 31.0059 16.0645L31.0059 3.67188C31.0059 1.24023 29.7656 0 27.2949 0L6.76758 0C4.6582 0 3.52539 1.19141 2.91992 3.00781L0.419922 10.5859C0.126953 11.4551 0 12.1875 0 13.3594ZM12.041 9.75586L12.041 5.52734C12.041 4.79492 12.4707 4.375 13.2031 4.375L24.8828 4.375C25.6152 4.375 26.0449 4.79492 26.0449 5.52734L26.0449 9.75586C26.0449 10.4492 25.6152 10.8789 24.8828 10.8789L13.2031 10.8789C12.4707 10.8789 12.041 10.4492 12.041 9.75586ZM4.14062 10.8789C3.44727 10.8789 3.11523 10.3223 3.37891 9.52148L4.78516 5.24414C4.95117 4.72656 5.21484 4.375 6.11328 4.375L7.70508 4.375C8.42773 4.375 8.85742 4.79492 8.85742 5.52734L8.85742 9.75586C8.85742 10.4492 8.42773 10.8789 7.70508 10.8789Z", fillAlpha = 0.85f)
        }
        return _sFTrainSideRearCar!!
    }

private var _sFTrainSideRearCar: ImageVector? = null
