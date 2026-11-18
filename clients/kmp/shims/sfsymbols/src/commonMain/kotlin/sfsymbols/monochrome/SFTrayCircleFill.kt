package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTrayCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFTrayCircleFill: ImageVector
    get() {
        if (_sFTrayCircleFill != null) {
            return _sFTrayCircleFill!!
        }
        _sFTrayCircleFill = sfIcon(
            name = "Monochrome.SFTrayCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7344C25.4395 19.7461 19.7266 25.459 12.7148 25.459C5.71289 25.459 0 19.7461 0 12.7344C0 5.73242 5.71289 0.0195312 12.7148 0.0195312C19.7266 0.0195312 25.4395 5.73242 25.4395 12.7344ZM10.0488 6.95312C8.98438 6.95312 8.56445 7.1875 7.87109 8.125L5.47852 11.3281C5.23438 11.6699 5.14648 11.9043 5.14648 12.4414L5.14648 15.9863C5.14648 17.2559 5.80078 17.9102 7.08984 17.9102L18.3594 17.9102C19.6582 17.9102 20.3125 17.2461 20.3125 15.9863L20.3125 12.4414C20.3125 11.9043 20.2246 11.6699 19.9707 11.3281L17.5781 8.125C16.875 7.1875 16.4648 6.95312 15.4004 6.95312ZM16.4941 8.61328L18.8379 11.7773C18.916 11.875 18.8672 12.041 18.6914 12.041L14.7852 12.041C14.4824 12.041 14.3457 12.2754 14.3457 12.5098L14.3457 12.6465C14.3457 13.584 13.6914 14.1992 12.7246 14.1992C11.7676 14.1992 11.1328 13.584 11.1328 12.6465L11.1328 12.5098C11.1328 12.2754 10.9668 12.041 10.6641 12.041L6.76758 12.041C6.58203 12.041 6.54297 11.8945 6.63086 11.7773L8.96484 8.61328C9.22852 8.25195 9.49219 8.0957 9.9707 8.0957L15.498 8.0957C15.9668 8.0957 16.2305 8.26172 16.4941 8.61328Z", fillAlpha = 0.85f)
        }
        return _sFTrayCircleFill!!
    }

private var _sFTrayCircleFill: ImageVector? = null
