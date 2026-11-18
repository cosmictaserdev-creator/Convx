package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFButtonHorizontalTop (monochrome)
 * Viewport: 30.2734 x 15.9082
 */
public val SfSymbols.Monochrome.SFButtonHorizontalTop: ImageVector
    get() {
        if (_sFButtonHorizontalTop != null) {
            return _sFButtonHorizontalTop!!
        }
        _sFButtonHorizontalTop = sfIcon(
            name = "Monochrome.SFButtonHorizontalTop",
            viewportWidth = 30.2734f,
            viewportHeight = 15.9082f
        ) {
            addSfPath("M23.5254 11.8457C27.2461 11.8457 29.9121 9.52148 29.9121 5.95703C29.9121 2.38281 27.2461 0.0683594 23.5254 0.0683594L6.38672 0.0683594C2.66602 0.0683594 0 2.38281 0 5.95703C0 9.52148 2.66602 11.8457 6.38672 11.8457ZM23.5254 10.1172L6.38672 10.1172C3.7207 10.1172 1.72852 8.47656 1.72852 5.95703C1.72852 3.44727 3.7207 1.79688 6.38672 1.79688L23.5254 1.79688C26.1914 1.79688 28.1836 3.44727 28.1836 5.95703C28.1836 8.47656 26.1914 10.1172 23.5254 10.1172ZM23.5254 15.9082C27.3926 15.9082 29.9121 13.2715 29.9121 9.21875L29.9121 5.95703L28.1836 5.95703L28.1836 9.21875C28.1836 12.207 26.3379 14.1797 23.5254 14.1797L6.38672 14.1797C3.57422 14.1797 1.72852 12.207 1.72852 9.21875L1.72852 5.95703L0 5.95703L0 9.21875C0 13.2715 2.51953 15.9082 6.38672 15.9082Z", fillAlpha = 0.85f)
        }
        return _sFButtonHorizontalTop!!
    }

private var _sFButtonHorizontalTop: ImageVector? = null
