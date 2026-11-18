package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SF1SquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SF1SquareFill: ImageVector
    get() {
        if (_sF1SquareFill != null) {
            return _sF1SquareFill!!
        }
        _sF1SquareFill = sfIcon(
            name = "Dualtone.SF1SquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M12.1387 17.5586C11.5918 17.5586 11.2598 17.1875 11.2598 16.5625L11.2598 7.35352L11.1719 7.35352L9.30664 8.58398C9.0625 8.74023 8.94531 8.76953 8.76953 8.76953C8.4082 8.76953 8.125 8.44727 8.125 8.0957C8.125 7.80273 8.23242 7.62695 8.47656 7.45117L10.8984 5.83008C11.377 5.49805 11.6699 5.39062 12.0898 5.39062C12.6855 5.39062 13.0469 5.75195 13.0469 6.38672L13.0469 16.5625C13.0469 17.1875 12.7148 17.5586 12.1387 17.5586Z", fillAlpha = 0.85f)
        }
        return _sF1SquareFill!!
    }

private var _sF1SquareFill: ImageVector? = null
