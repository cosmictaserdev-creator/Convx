package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTrayCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFTrayCircleFill: ImageVector
    get() {
        if (_sFTrayCircleFill != null) {
            return _sFTrayCircleFill!!
        }
        _sFTrayCircleFill = sfIcon(
            name = "Dualtone.SFTrayCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.459C19.7266 25.459 25.4395 19.7461 25.4395 12.7344C25.4395 5.73242 19.7266 0.0195312 12.7148 0.0195312C5.71289 0.0195312 0 5.73242 0 12.7344C0 19.7461 5.71289 25.459 12.7148 25.459Z", fillAlpha = 0.2125f)
            addSfPath("M7.08984 17.9102C5.80078 17.9102 5.14648 17.2559 5.14648 15.9863L5.14648 12.4414C5.14648 11.9043 5.23438 11.6699 5.47852 11.3281L7.87109 8.125C8.56445 7.1875 8.98438 6.95312 10.0488 6.95312L15.4004 6.95312C16.4648 6.95312 16.875 7.1875 17.5781 8.125L19.9707 11.3281C20.2246 11.6699 20.3125 11.9043 20.3125 12.4414L20.3125 15.9863C20.3125 17.2461 19.6582 17.9102 18.3594 17.9102ZM6.76758 12.041L10.6641 12.041C10.9668 12.041 11.1328 12.2754 11.1328 12.5098L11.1328 12.6465C11.1328 13.584 11.7676 14.1992 12.7246 14.1992C13.6914 14.1992 14.3457 13.584 14.3457 12.6465L14.3457 12.5098C14.3457 12.2754 14.4824 12.041 14.7852 12.041L18.6914 12.041C18.8672 12.041 18.916 11.875 18.8379 11.7773L16.4941 8.61328C16.2305 8.26172 15.9668 8.0957 15.498 8.0957L9.9707 8.0957C9.49219 8.0957 9.22852 8.25195 8.96484 8.61328L6.63086 11.7773C6.54297 11.8945 6.58203 12.041 6.76758 12.041Z", fillAlpha = 0.85f)
        }
        return _sFTrayCircleFill!!
    }

private var _sFTrayCircleFill: ImageVector? = null
