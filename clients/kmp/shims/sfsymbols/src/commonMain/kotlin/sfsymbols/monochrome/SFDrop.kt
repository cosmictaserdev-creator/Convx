package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDrop (monochrome)
 * Viewport: 17.5293 x 25.2051
 */
public val SfSymbols.Monochrome.SFDrop: ImageVector
    get() {
        if (_sFDrop != null) {
            return _sFDrop!!
        }
        _sFDrop = sfIcon(
            name = "Monochrome.SFDrop",
            viewportWidth = 17.5293f,
            viewportHeight = 25.2051f
        ) {
            addSfPath("M8.58398 25.2051C13.7402 25.2051 17.168 21.8555 17.168 16.8359C17.168 14.4434 16.2207 11.9434 15.3809 10.0684C14.043 7.08008 11.9922 3.80859 10.1562 0.976562C9.74609 0.351562 9.26758 0 8.58398 0C7.89062 0 7.42188 0.351562 7.02148 0.976562C5.17578 3.80859 3.125 7.08008 1.78711 10.0684C0.947266 11.9434 0 14.4434 0 16.8359C0 21.8555 3.4375 25.2051 8.58398 25.2051ZM8.58398 23.4473C4.49219 23.4473 1.75781 20.8008 1.75781 16.8359C1.75781 14.8242 2.55859 12.7539 3.35938 10.9277C4.75586 7.72461 6.60156 4.94141 8.38867 2.24609C8.50586 2.06055 8.68164 2.06055 8.79883 2.24609C10.5664 4.94141 12.4219 7.72461 13.7988 10.918C14.6094 12.7539 15.4102 14.8242 15.4102 16.8359C15.4102 20.8008 12.6855 23.4473 8.58398 23.4473Z", fillAlpha = 0.85f)
        }
        return _sFDrop!!
    }

private var _sFDrop: ImageVector? = null
