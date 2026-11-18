package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDropDegreesignFill (monochrome)
 * Viewport: 21.2891 x 31.2012
 */
public val SfSymbols.Monochrome.SFDropDegreesignFill: ImageVector
    get() {
        if (_sFDropDegreesignFill != null) {
            return _sFDropDegreesignFill!!
        }
        _sFDropDegreesignFill = sfIcon(
            name = "Monochrome.SFDropDegreesignFill",
            viewportWidth = 21.2891f,
            viewportHeight = 31.2012f
        ) {
            addSfPath("M8.58398 28.2031C13.7402 28.2031 17.168 24.8535 17.168 19.834C17.168 17.4414 16.2207 14.9414 15.3809 13.0664C14.043 10.0781 11.9922 6.80664 10.1562 3.97461C9.74609 3.34961 9.26758 2.99805 8.58398 2.99805C7.89062 2.99805 7.42188 3.34961 7.02148 3.97461C5.17578 6.80664 3.125 10.0781 1.78711 13.0664C0.947266 14.9414 0 17.4414 0 19.834C0 24.8535 3.4375 28.2031 8.58398 28.2031ZM17.2461 7.37305C19.3164 7.37305 20.9277 5.75195 20.9277 3.69141C20.9277 1.62109 19.3164 0 17.2461 0C15.1855 0 13.5645 1.62109 13.5645 3.69141C13.5645 5.75195 15.1855 7.37305 17.2461 7.37305ZM17.2461 5.83984C16.0449 5.83984 15.0977 4.90234 15.0977 3.69141C15.0977 2.48047 16.0449 1.5332 17.2461 1.5332C18.457 1.5332 19.3945 2.48047 19.3945 3.69141C19.3945 4.90234 18.457 5.83984 17.2461 5.83984Z", fillAlpha = 0.85f)
        }
        return _sFDropDegreesignFill!!
    }

private var _sFDropDegreesignFill: ImageVector? = null
