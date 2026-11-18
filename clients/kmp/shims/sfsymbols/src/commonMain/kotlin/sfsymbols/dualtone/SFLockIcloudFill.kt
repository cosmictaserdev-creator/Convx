package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLockIcloudFill (dualtone)
 * Viewport: 31.5625 x 23.3496
 */
public val SfSymbols.Dualtone.SFLockIcloudFill: ImageVector
    get() {
        if (_sFLockIcloudFill != null) {
            return _sFLockIcloudFill!!
        }
        _sFLockIcloudFill = sfIcon(
            name = "Dualtone.SFLockIcloudFill",
            viewportWidth = 31.5625f,
            viewportHeight = 23.3496f
        ) {
            addSfPath("M24.5801 21.4746C28.3008 21.4746 31.2012 18.7891 31.2012 15.4102C31.2012 12.8223 29.7363 10.5566 27.2559 9.58008C27.3242 4.0332 23.3594 0 18.3008 0C14.873 0 12.4805 1.85547 11.0352 4.0918C7.98828 3.10547 4.62891 5.44922 4.58984 8.94531C1.78711 9.33594 0 11.7871 0 14.8242C0 18.4668 3.16406 21.4648 7.37305 21.4648Z", fillAlpha = 0.2125f)
            addSfPath("M12.7051 17.5684C11.9043 17.5684 11.4941 17.1484 11.4941 16.2793L11.4941 11.7578C11.4941 10.9473 11.8457 10.5371 12.5488 10.4883L12.5488 9.12109C12.5488 7.07031 13.7891 5.70312 15.6055 5.70312C17.4316 5.70312 18.6719 7.07031 18.6719 9.12109L18.6719 10.4883C19.3652 10.5371 19.7168 10.9473 19.7168 11.7578L19.7168 16.2793C19.7168 17.1484 19.3164 17.5684 18.5254 17.5684ZM13.6523 10.4785L17.5488 10.4785L17.5488 9.01367C17.5488 7.66602 16.7773 6.79688 15.6055 6.79688C14.4434 6.79688 13.6523 7.66602 13.6523 9.01367Z", fillAlpha = 0.85f)
        }
        return _sFLockIcloudFill!!
    }

private var _sFLockIcloudFill: ImageVector? = null
