package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDecreaseQuotelevel (monochrome)
 * Viewport: 13.1836 x 23.5254
 */
public val SfSymbols.Monochrome.SFDecreaseQuotelevel: ImageVector
    get() {
        if (_sFDecreaseQuotelevel != null) {
            return _sFDecreaseQuotelevel!!
        }
        _sFDecreaseQuotelevel = sfIcon(
            name = "Monochrome.SFDecreaseQuotelevel",
            viewportWidth = 13.1836f,
            viewportHeight = 23.5254f
        ) {
            addSfPath("M0.869141 23.4668C1.35742 23.4668 1.72852 23.0762 1.72852 22.5879L1.72852 0.869141C1.72852 0.380859 1.35742 0 0.869141 0C0.371094 0 0 0.380859 0 0.869141L0 22.5879C0 23.0762 0.371094 23.4668 0.869141 23.4668Z", fillAlpha = 0.85f)
            addSfPath("M10.9277 15.4199C11.8164 16.0156 12.8223 15.6738 12.8223 14.6094L12.8223 8.89648C12.8223 7.7832 11.8555 7.44141 10.9277 8.06641L6.94336 10.7812C6.25977 11.25 6.23047 12.2754 6.94336 12.7539Z", fillAlpha = 0.85f)
        }
        return _sFDecreaseQuotelevel!!
    }

private var _sFDecreaseQuotelevel: ImageVector? = null
