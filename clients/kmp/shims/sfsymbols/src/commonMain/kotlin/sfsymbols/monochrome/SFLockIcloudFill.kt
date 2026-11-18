package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLockIcloudFill (monochrome)
 * Viewport: 31.5625 x 23.3496
 */
public val SfSymbols.Monochrome.SFLockIcloudFill: ImageVector
    get() {
        if (_sFLockIcloudFill != null) {
            return _sFLockIcloudFill!!
        }
        _sFLockIcloudFill = sfIcon(
            name = "Monochrome.SFLockIcloudFill",
            viewportWidth = 31.5625f,
            viewportHeight = 23.3496f
        ) {
            addSfPath("M27.2559 9.58008C29.7363 10.5566 31.2012 12.8223 31.2012 15.4102C31.2012 18.7891 28.3008 21.4746 24.5801 21.4746L7.37305 21.4648C3.16406 21.4648 0 18.4668 0 14.8242C0 11.7871 1.78711 9.33594 4.58984 8.94531C4.62891 5.44922 7.98828 3.10547 11.0352 4.0918C12.4805 1.85547 14.873 0 18.3008 0C23.3594 0 27.3242 4.0332 27.2559 9.58008ZM12.5488 9.12109L12.5488 10.4883C11.8457 10.5371 11.4941 10.9473 11.4941 11.7578L11.4941 16.2793C11.4941 17.1484 11.9043 17.5684 12.7051 17.5684L18.5254 17.5684C19.3164 17.5684 19.7168 17.1484 19.7168 16.2793L19.7168 11.7578C19.7168 10.9473 19.3652 10.5371 18.6719 10.4883L18.6719 9.12109C18.6719 7.07031 17.4316 5.70312 15.6055 5.70312C13.7891 5.70312 12.5488 7.07031 12.5488 9.12109ZM17.5488 9.01367L17.5488 10.4785L13.6523 10.4785L13.6523 9.01367C13.6523 7.66602 14.4434 6.79688 15.6055 6.79688C16.7773 6.79688 17.5488 7.66602 17.5488 9.01367Z", fillAlpha = 0.85f)
        }
        return _sFLockIcloudFill!!
    }

private var _sFLockIcloudFill: ImageVector? = null
