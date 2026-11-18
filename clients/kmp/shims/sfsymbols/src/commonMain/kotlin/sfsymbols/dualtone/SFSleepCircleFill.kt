package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSleepCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFSleepCircleFill: ImageVector
    get() {
        if (_sFSleepCircleFill != null) {
            return _sFSleepCircleFill!!
        }
        _sFSleepCircleFill = sfIcon(
            name = "Dualtone.SFSleepCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.459C19.7266 25.459 25.4395 19.7461 25.4395 12.7344C25.4395 5.73242 19.7266 0.0195312 12.7148 0.0195312C5.71289 0.0195312 0 5.73242 0 12.7344C0 19.7461 5.71289 25.459 12.7148 25.459Z", fillAlpha = 0.2125f)
            addSfPath("M12.7246 20.6445C8.35938 20.6445 4.81445 17.0996 4.81445 12.7344C4.81445 8.36914 8.35938 4.83398 12.7246 4.83398C17.0898 4.83398 20.6348 8.36914 20.6348 12.7344C20.6348 17.0996 17.0898 20.6445 12.7246 20.6445ZM6.44531 12.7344C6.44531 13.5449 6.5918 14.3066 6.86523 15.0098L18.6035 15.0098C18.877 14.3066 19.0332 13.5449 19.0332 12.7344C19.0332 9.25781 16.2109 6.44531 12.7344 6.44531C9.25781 6.44531 6.44531 9.25781 6.44531 12.7344ZM7.70508 16.4941C8.84766 18.0371 10.6738 19.0332 12.7344 19.0332C14.7949 19.0332 16.6309 18.0371 17.7637 16.4941Z", fillAlpha = 0.85f)
        }
        return _sFSleepCircleFill!!
    }

private var _sFSleepCircleFill: ImageVector? = null
