package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPersonIcloudFill (monochrome)
 * Viewport: 31.5625 x 23.3496
 */
public val SfSymbols.Monochrome.SFPersonIcloudFill: ImageVector
    get() {
        if (_sFPersonIcloudFill != null) {
            return _sFPersonIcloudFill!!
        }
        _sFPersonIcloudFill = sfIcon(
            name = "Monochrome.SFPersonIcloudFill",
            viewportWidth = 31.5625f,
            viewportHeight = 23.3496f
        ) {
            addSfPath("M27.2559 9.58008C29.7363 10.5566 31.2012 12.8223 31.2012 15.4102C31.2012 18.7891 28.3008 21.4746 24.5801 21.4746L7.37305 21.4648C3.16406 21.4648 0 18.4668 0 14.8242C0 11.7871 1.78711 9.33594 4.58984 8.94531C4.62891 5.44922 7.98828 3.10547 11.0352 4.0918C12.4805 1.85547 14.873 0 18.3008 0C23.3594 0 27.3242 4.0332 27.2559 9.58008ZM9.36523 19.5117L23.4863 19.5117C22.5098 17.3242 20.1465 15.1465 16.4258 15.1465C12.7051 15.1465 10.3418 17.3242 9.36523 19.5117ZM13.0664 9.92188C13.0762 11.9336 14.5703 13.584 16.4258 13.6035C18.291 13.623 19.7852 11.9336 19.7852 9.92188C19.7852 7.94922 18.291 6.33789 16.4258 6.33789C14.5703 6.33789 13.0664 7.94922 13.0664 9.92188Z", fillAlpha = 0.85f)
        }
        return _sFPersonIcloudFill!!
    }

private var _sFPersonIcloudFill: ImageVector? = null
