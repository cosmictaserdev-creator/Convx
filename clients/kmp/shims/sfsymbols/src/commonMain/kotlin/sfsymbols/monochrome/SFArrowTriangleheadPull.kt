package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowTriangleheadPull (monochrome)
 * Viewport: 12.8828 x 24.2114
 */
public val SfSymbols.Monochrome.SFArrowTriangleheadPull: ImageVector
    get() {
        if (_sFArrowTriangleheadPull != null) {
            return _sFArrowTriangleheadPull!!
        }
        _sFArrowTriangleheadPull = sfIcon(
            name = "Monochrome.SFArrowTriangleheadPull",
            viewportWidth = 12.8828f,
            viewportHeight = 24.2114f
        ) {
            addSfPath("M0.801772 5.51881L6.48537 5.51881C7.27638 5.51881 7.49123 4.97193 7.05177 4.34693L4.27833 0.391854C3.91701-0.125724 3.3799-0.135489 3.0088 0.391854L0.235366 4.33717C-0.223619 4.97193 0.000990731 5.51881 0.801772 5.51881ZM11.2901 20.0403C11.8955 20.2551 12.3447 19.884 12.4815 19.4251C12.6084 18.9856 12.4522 18.468 11.8858 18.2727C6.72951 16.5149 4.55177 13.6047 4.55177 8.85865L4.55177 3.96607C4.55177 3.46803 4.13185 3.05787 3.6338 3.05787C3.12599 3.05787 2.71583 3.46803 2.71583 3.96607L2.71583 8.85865C2.71583 14.4544 5.41115 18.0383 11.2901 20.0403ZM4.55177 3.96607C4.55177 3.46803 4.13185 3.05787 3.6338 3.05787C3.12599 3.05787 2.71583 3.46803 2.71583 3.96607L2.71583 23.2825C2.71583 23.7903 3.12599 24.2004 3.6338 24.2004C4.13185 24.2004 4.55177 23.7903 4.55177 23.2825Z", fillAlpha = 0.85f)
        }
        return _sFArrowTriangleheadPull!!
    }

private var _sFArrowTriangleheadPull: ImageVector? = null
