package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCheckmarkIcloudFill (monochrome)
 * Viewport: 31.5625 x 23.3496
 */
public val SfSymbols.Monochrome.SFCheckmarkIcloudFill: ImageVector
    get() {
        if (_sFCheckmarkIcloudFill != null) {
            return _sFCheckmarkIcloudFill!!
        }
        _sFCheckmarkIcloudFill = sfIcon(
            name = "Monochrome.SFCheckmarkIcloudFill",
            viewportWidth = 31.5625f,
            viewportHeight = 23.3496f
        ) {
            addSfPath("M27.2559 9.58008C29.7363 10.5566 31.2012 12.8223 31.2012 15.4102C31.2012 18.7891 28.3008 21.4746 24.5801 21.4746L7.37305 21.4648C3.16406 21.4648 0 18.4668 0 14.8242C0 11.7871 1.78711 9.33594 4.58984 8.94531C4.62891 5.44922 7.98828 3.10547 11.0352 4.0918C12.4805 1.85547 14.873 0 18.3008 0C23.3594 0 27.3242 4.0332 27.2559 9.58008ZM19.1699 6.80664L14.541 14.502L12.2266 11.5234C12.0215 11.25 11.7578 11.1035 11.4258 11.1035C10.9473 11.1035 10.5371 11.4746 10.5371 11.9922C10.5371 12.2266 10.625 12.4512 10.791 12.6758L13.6816 16.2695C13.9551 16.6113 14.2383 16.748 14.5898 16.748C14.9512 16.748 15.2539 16.5918 15.459 16.25L20.625 7.83203C20.7324 7.65625 20.8594 7.41211 20.8594 7.1582C20.8594 6.67969 20.4492 6.31836 19.9512 6.31836C19.6387 6.31836 19.3457 6.49414 19.1699 6.80664Z", fillAlpha = 0.85f)
        }
        return _sFCheckmarkIcloudFill!!
    }

private var _sFCheckmarkIcloudFill: ImageVector? = null
