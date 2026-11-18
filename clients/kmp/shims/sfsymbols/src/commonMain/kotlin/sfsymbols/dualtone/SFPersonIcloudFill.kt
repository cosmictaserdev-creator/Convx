package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPersonIcloudFill (dualtone)
 * Viewport: 31.5625 x 23.3496
 */
public val SfSymbols.Dualtone.SFPersonIcloudFill: ImageVector
    get() {
        if (_sFPersonIcloudFill != null) {
            return _sFPersonIcloudFill!!
        }
        _sFPersonIcloudFill = sfIcon(
            name = "Dualtone.SFPersonIcloudFill",
            viewportWidth = 31.5625f,
            viewportHeight = 23.3496f
        ) {
            addSfPath("M24.5801 21.4746C28.3008 21.4746 31.2012 18.7891 31.2012 15.4102C31.2012 12.8223 29.7363 10.5566 27.2559 9.58008C27.3242 4.0332 23.3594 0 18.3008 0C14.873 0 12.4805 1.85547 11.0352 4.0918C7.98828 3.10547 4.62891 5.44922 4.58984 8.94531C1.78711 9.33594 0 11.7871 0 14.8242C0 18.4668 3.16406 21.4648 7.37305 21.4648Z", fillAlpha = 0.2125f)
            addSfPath("M9.36523 19.5117C10.3418 17.3242 12.7051 15.1465 16.4258 15.1465C20.1465 15.1465 22.5098 17.3242 23.4863 19.5117ZM16.4258 13.6035C14.5703 13.584 13.0762 11.9336 13.0664 9.92188C13.0664 7.94922 14.5703 6.33789 16.4258 6.33789C18.291 6.33789 19.7852 7.94922 19.7852 9.92188C19.7852 11.9336 18.291 13.623 16.4258 13.6035Z", fillAlpha = 0.85f)
        }
        return _sFPersonIcloudFill!!
    }

private var _sFPersonIcloudFill: ImageVector? = null
