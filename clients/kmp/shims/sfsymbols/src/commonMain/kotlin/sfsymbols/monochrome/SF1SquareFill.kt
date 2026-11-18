package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SF1SquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SF1SquareFill: ImageVector
    get() {
        if (_sF1SquareFill != null) {
            return _sF1SquareFill!!
        }
        _sF1SquareFill = sfIcon(
            name = "Monochrome.SF1SquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM10.8984 5.83008L8.47656 7.45117C8.23242 7.62695 8.125 7.80273 8.125 8.0957C8.125 8.44727 8.4082 8.76953 8.76953 8.76953C8.94531 8.76953 9.0625 8.74023 9.30664 8.58398L11.1719 7.35352L11.2598 7.35352L11.2598 16.5625C11.2598 17.1875 11.5918 17.5586 12.1387 17.5586C12.7148 17.5586 13.0469 17.1875 13.0469 16.5625L13.0469 6.38672C13.0469 5.75195 12.6855 5.39062 12.0898 5.39062C11.6699 5.39062 11.377 5.49805 10.8984 5.83008Z", fillAlpha = 0.85f)
        }
        return _sF1SquareFill!!
    }

private var _sF1SquareFill: ImageVector? = null
