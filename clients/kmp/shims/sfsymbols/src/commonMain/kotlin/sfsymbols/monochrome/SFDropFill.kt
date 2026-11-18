package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDropFill (monochrome)
 * Viewport: 17.5293 x 25.2051
 */
public val SfSymbols.Monochrome.SFDropFill: ImageVector
    get() {
        if (_sFDropFill != null) {
            return _sFDropFill!!
        }
        _sFDropFill = sfIcon(
            name = "Monochrome.SFDropFill",
            viewportWidth = 17.5293f,
            viewportHeight = 25.2051f
        ) {
            addSfPath("M8.58398 25.2051C13.7402 25.2051 17.168 21.8555 17.168 16.8359C17.168 14.4434 16.2207 11.9434 15.3809 10.0684C14.043 7.08008 11.8359 3.80859 9.90234 0.839844C9.55078 0.283203 9.0918 0 8.58398 0C8.07617 0 7.62695 0.283203 7.26562 0.839844C5.33203 3.80859 3.125 7.08008 1.78711 10.0684C0.947266 11.9434 0 14.4434 0 16.8359C0 21.8555 3.4375 25.2051 8.58398 25.2051Z", fillAlpha = 0.85f)
        }
        return _sFDropFill!!
    }

private var _sFDropFill: ImageVector? = null
