package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFIcloudFill (monochrome)
 * Viewport: 31.5625 x 23.3496
 */
public val SfSymbols.Monochrome.SFIcloudFill: ImageVector
    get() {
        if (_sFIcloudFill != null) {
            return _sFIcloudFill!!
        }
        _sFIcloudFill = sfIcon(
            name = "Monochrome.SFIcloudFill",
            viewportWidth = 31.5625f,
            viewportHeight = 23.3496f
        ) {
            addSfPath("M24.5801 21.4746C28.3008 21.4746 31.2012 18.7891 31.2012 15.4102C31.2012 12.8223 29.7363 10.5566 27.2559 9.58008C27.3242 4.0332 23.3594 0 18.3008 0C14.873 0 12.4805 1.85547 11.0352 4.0918C7.98828 3.10547 4.62891 5.44922 4.58984 8.94531C1.78711 9.33594 0 11.7871 0 14.8242C0 18.4668 3.16406 21.4648 7.37305 21.4648Z", fillAlpha = 0.85f)
        }
        return _sFIcloudFill!!
    }

private var _sFIcloudFill: ImageVector? = null
