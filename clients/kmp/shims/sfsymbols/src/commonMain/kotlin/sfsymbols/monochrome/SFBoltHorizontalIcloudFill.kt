package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBoltHorizontalIcloudFill (monochrome)
 * Viewport: 31.5625 x 23.3496
 */
public val SfSymbols.Monochrome.SFBoltHorizontalIcloudFill: ImageVector
    get() {
        if (_sFBoltHorizontalIcloudFill != null) {
            return _sFBoltHorizontalIcloudFill!!
        }
        _sFBoltHorizontalIcloudFill = sfIcon(
            name = "Monochrome.SFBoltHorizontalIcloudFill",
            viewportWidth = 31.5625f,
            viewportHeight = 23.3496f
        ) {
            addSfPath("M27.2559 9.58008C29.7363 10.5566 31.2012 12.8223 31.2012 15.4102C31.2012 18.7891 28.3008 21.4746 24.5801 21.4746L7.37305 21.4648C3.16406 21.4648 0 18.4668 0 14.8242C0 11.7871 1.78711 9.33594 4.58984 8.94531C4.62891 5.44922 7.98828 3.10547 11.0352 4.0918C12.4805 1.85547 14.873 0 18.3008 0C23.3594 0 27.3242 4.0332 27.2559 9.58008ZM11.6992 8.56445L7.31445 14.2188C6.79688 14.9023 7.46094 15.5078 8.11523 15.1562L12.4316 12.7832L18.1738 15.8594C18.3984 15.9766 18.5938 16.0547 18.7793 16.0547C19.0332 16.0547 19.2773 15.9277 19.502 15.6445L23.8965 9.9707C24.375 9.3457 23.7793 8.69141 23.0859 9.0625L18.7598 11.4258L13.0176 8.33984C12.8027 8.23242 12.6074 8.1543 12.4121 8.1543C12.168 8.1543 11.9336 8.28125 11.6992 8.56445Z", fillAlpha = 0.85f)
        }
        return _sFBoltHorizontalIcloudFill!!
    }

private var _sFBoltHorizontalIcloudFill: ImageVector? = null
