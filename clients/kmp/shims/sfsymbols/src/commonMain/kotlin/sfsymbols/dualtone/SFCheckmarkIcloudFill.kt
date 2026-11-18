package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCheckmarkIcloudFill (dualtone)
 * Viewport: 31.5625 x 23.3496
 */
public val SfSymbols.Dualtone.SFCheckmarkIcloudFill: ImageVector
    get() {
        if (_sFCheckmarkIcloudFill != null) {
            return _sFCheckmarkIcloudFill!!
        }
        _sFCheckmarkIcloudFill = sfIcon(
            name = "Dualtone.SFCheckmarkIcloudFill",
            viewportWidth = 31.5625f,
            viewportHeight = 23.3496f
        ) {
            addSfPath("M24.5801 21.4746C28.3008 21.4746 31.2012 18.7891 31.2012 15.4102C31.2012 12.8223 29.7363 10.5566 27.2559 9.58008C27.3242 4.0332 23.3594 0 18.3008 0C14.873 0 12.4805 1.85547 11.0352 4.0918C7.98828 3.10547 4.62891 5.44922 4.58984 8.94531C1.78711 9.33594 0 11.7871 0 14.8242C0 18.4668 3.16406 21.4648 7.37305 21.4648Z", fillAlpha = 0.2125f)
            addSfPath("M14.5898 16.748C14.2383 16.748 13.9551 16.6113 13.6816 16.2695L10.791 12.6758C10.625 12.4512 10.5371 12.2266 10.5371 11.9922C10.5371 11.4746 10.9473 11.1035 11.4258 11.1035C11.7578 11.1035 12.0215 11.25 12.2266 11.5234L14.541 14.502L19.1699 6.80664C19.3457 6.49414 19.6387 6.31836 19.9512 6.31836C20.4492 6.31836 20.8594 6.67969 20.8594 7.1582C20.8594 7.41211 20.7324 7.65625 20.625 7.83203L15.459 16.25C15.2539 16.5918 14.9512 16.748 14.5898 16.748Z", fillAlpha = 0.85f)
        }
        return _sFCheckmarkIcloudFill!!
    }

private var _sFCheckmarkIcloudFill: ImageVector? = null
