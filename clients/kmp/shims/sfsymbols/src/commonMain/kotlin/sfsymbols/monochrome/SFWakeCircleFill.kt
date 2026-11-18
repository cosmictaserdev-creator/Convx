package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFWakeCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFWakeCircleFill: ImageVector
    get() {
        if (_sFWakeCircleFill != null) {
            return _sFWakeCircleFill!!
        }
        _sFWakeCircleFill = sfIcon(
            name = "Monochrome.SFWakeCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7344C25.4395 19.7461 19.7266 25.459 12.7148 25.459C5.71289 25.459 0 19.7461 0 12.7344C0 5.73242 5.71289 0.0195312 12.7148 0.0195312C19.7266 0.0195312 25.4395 5.73242 25.4395 12.7344ZM4.81445 12.7344C4.81445 17.0996 8.35938 20.6445 12.7246 20.6445C17.0898 20.6445 20.6348 17.0996 20.6348 12.7344C20.6348 8.36914 17.0898 4.83398 12.7246 4.83398C8.35938 4.83398 4.81445 8.36914 4.81445 12.7344ZM19.0332 12.7344C19.0332 16.2109 16.2109 19.0332 12.7344 19.0332C9.25781 19.0332 6.44531 16.2109 6.44531 12.7344C6.44531 11.9336 6.5918 11.1719 6.86523 10.4688L18.6035 10.4688C18.877 11.1719 19.0332 11.9336 19.0332 12.7344ZM17.7637 8.97461L7.70508 8.97461C8.84766 7.44141 10.6738 6.44531 12.7344 6.44531C14.7949 6.44531 16.6309 7.44141 17.7637 8.97461Z", fillAlpha = 0.85f)
        }
        return _sFWakeCircleFill!!
    }

private var _sFWakeCircleFill: ImageVector? = null
