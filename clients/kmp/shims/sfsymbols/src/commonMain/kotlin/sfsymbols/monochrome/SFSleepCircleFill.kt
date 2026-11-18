package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSleepCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFSleepCircleFill: ImageVector
    get() {
        if (_sFSleepCircleFill != null) {
            return _sFSleepCircleFill!!
        }
        _sFSleepCircleFill = sfIcon(
            name = "Monochrome.SFSleepCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7344C25.4395 19.7461 19.7266 25.459 12.7148 25.459C5.71289 25.459 0 19.7461 0 12.7344C0 5.73242 5.71289 0.0195312 12.7148 0.0195312C19.7266 0.0195312 25.4395 5.73242 25.4395 12.7344ZM4.81445 12.7344C4.81445 17.0996 8.35938 20.6445 12.7246 20.6445C17.0898 20.6445 20.6348 17.0996 20.6348 12.7344C20.6348 8.36914 17.0898 4.83398 12.7246 4.83398C8.35938 4.83398 4.81445 8.36914 4.81445 12.7344ZM12.7344 19.0332C10.6738 19.0332 8.84766 18.0371 7.70508 16.4941L17.7637 16.4941C16.6309 18.0371 14.7949 19.0332 12.7344 19.0332ZM19.0332 12.7344C19.0332 13.5449 18.877 14.3066 18.6035 15.0098L6.86523 15.0098C6.5918 14.3066 6.44531 13.5449 6.44531 12.7344C6.44531 9.25781 9.25781 6.44531 12.7344 6.44531C16.2109 6.44531 19.0332 9.25781 19.0332 12.7344Z", fillAlpha = 0.85f)
        }
        return _sFSleepCircleFill!!
    }

private var _sFSleepCircleFill: ImageVector? = null
