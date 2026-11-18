package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFWakeCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFWakeCircleFill: ImageVector
    get() {
        if (_sFWakeCircleFill != null) {
            return _sFWakeCircleFill!!
        }
        _sFWakeCircleFill = sfIcon(
            name = "Dualtone.SFWakeCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.459C19.7266 25.459 25.4395 19.7461 25.4395 12.7344C25.4395 5.73242 19.7266 0.0195312 12.7148 0.0195312C5.71289 0.0195312 0 5.73242 0 12.7344C0 19.7461 5.71289 25.459 12.7148 25.459Z", fillAlpha = 0.2125f)
            addSfPath("M12.7246 4.83398C17.0898 4.83398 20.6348 8.36914 20.6348 12.7344C20.6348 17.0996 17.0898 20.6445 12.7246 20.6445C8.35938 20.6445 4.81445 17.0996 4.81445 12.7344C4.81445 8.36914 8.35938 4.83398 12.7246 4.83398ZM6.44531 12.7344C6.44531 16.2109 9.25781 19.0332 12.7344 19.0332C16.2109 19.0332 19.0332 16.2109 19.0332 12.7344C19.0332 11.9336 18.877 11.1719 18.6035 10.4688L6.86523 10.4688C6.5918 11.1719 6.44531 11.9336 6.44531 12.7344ZM7.70508 8.97461L17.7637 8.97461C16.6309 7.44141 14.7949 6.44531 12.7344 6.44531C10.6738 6.44531 8.84766 7.44141 7.70508 8.97461Z", fillAlpha = 0.85f)
        }
        return _sFWakeCircleFill!!
    }

private var _sFWakeCircleFill: ImageVector? = null
