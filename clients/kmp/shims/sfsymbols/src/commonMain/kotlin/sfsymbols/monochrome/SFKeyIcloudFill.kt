package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFKeyIcloudFill (monochrome)
 * Viewport: 31.5625 x 23.3496
 */
public val SfSymbols.Monochrome.SFKeyIcloudFill: ImageVector
    get() {
        if (_sFKeyIcloudFill != null) {
            return _sFKeyIcloudFill!!
        }
        _sFKeyIcloudFill = sfIcon(
            name = "Monochrome.SFKeyIcloudFill",
            viewportWidth = 31.5625f,
            viewportHeight = 23.3496f
        ) {
            addSfPath("M27.2559 9.58008C29.7363 10.5566 31.2012 12.8223 31.2012 15.4102C31.2012 18.7891 28.3008 21.4746 24.5801 21.4746L7.37305 21.4648C3.16406 21.4648 0 18.4668 0 14.8242C0 11.7871 1.78711 9.33594 4.58984 8.94531C4.62891 5.44922 7.98828 3.10547 11.0352 4.0918C12.4805 1.85547 14.873 0 18.3008 0C23.3594 0 27.3242 4.0332 27.2559 9.58008ZM15.1953 10.8691L9.26758 10.8691C9.13086 10.8691 9.02344 10.918 8.89648 11.0352L7.91016 12.0117C7.80273 12.1289 7.79297 12.2949 7.91016 12.4219L9.78516 14.3066C9.90234 14.4336 10.0684 14.4336 10.1855 14.3066L11.377 13.1055L12.998 14.7461C13.0957 14.8633 13.252 14.8438 13.3887 14.7266L15.0293 13.1055C15.752 14.9707 17.1191 16.0059 18.7598 16.0059C20.8496 16.0059 22.5391 14.3164 22.5391 12.2168C22.5391 10.1074 20.8496 8.41797 18.7598 8.41797C17.1582 8.41797 15.752 9.375 15.1953 10.8691ZM20.7129 12.207C20.7129 12.8516 20.1855 13.3691 19.5508 13.3691C18.8867 13.3691 18.3789 12.8516 18.3789 12.207C18.3789 11.5625 18.8867 11.0449 19.5508 11.0449C20.1855 11.0449 20.7129 11.5527 20.7129 12.207Z", fillAlpha = 0.85f)
        }
        return _sFKeyIcloudFill!!
    }

private var _sFKeyIcloudFill: ImageVector? = null
