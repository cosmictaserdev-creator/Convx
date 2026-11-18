package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFXmarkIcloudFill (monochrome)
 * Viewport: 31.5625 x 23.3496
 */
public val SfSymbols.Monochrome.SFXmarkIcloudFill: ImageVector
    get() {
        if (_sFXmarkIcloudFill != null) {
            return _sFXmarkIcloudFill!!
        }
        _sFXmarkIcloudFill = sfIcon(
            name = "Monochrome.SFXmarkIcloudFill",
            viewportWidth = 31.5625f,
            viewportHeight = 23.3496f
        ) {
            addSfPath("M27.2559 9.58008C29.7363 10.5566 31.2012 12.8223 31.2012 15.4102C31.2012 18.7891 28.3008 21.4746 24.5801 21.4746L7.37305 21.4648C3.16406 21.4648 0 18.4668 0 14.8242C0 11.7871 1.78711 9.33594 4.58984 8.94531C4.62891 5.44922 7.98828 3.10547 11.0352 4.0918C12.4805 1.85547 14.873 0 18.3008 0C23.3594 0 27.3242 4.0332 27.2559 9.58008ZM19.0527 8.0957L15.5985 11.5332L12.1484 8.0957C11.9922 7.92969 11.7969 7.85156 11.582 7.85156C11.1328 7.85156 10.7715 8.20312 10.7715 8.64258C10.7715 8.86719 10.8496 9.0625 11.0059 9.21875L14.4545 12.6716L11.0059 16.1035C10.8496 16.2695 10.7715 16.4551 10.7715 16.6797C10.7715 17.1289 11.1328 17.4902 11.582 17.4902C11.8164 17.4902 12.002 17.4023 12.168 17.2461L15.5957 13.8142L19.0234 17.2461C19.1895 17.4023 19.3848 17.4902 19.6191 17.4902C20.0684 17.4902 20.4297 17.1289 20.4297 16.6797C20.4297 16.4551 20.3418 16.2695 20.1855 16.1035L16.739 12.6695L20.1855 9.21875C20.3418 9.0625 20.4297 8.86719 20.4297 8.64258C20.4297 8.20312 20.0684 7.85156 19.6191 7.85156C19.3945 7.85156 19.209 7.92969 19.0527 8.0957Z", fillAlpha = 0.85f)
        }
        return _sFXmarkIcloudFill!!
    }

private var _sFXmarkIcloudFill: ImageVector? = null
