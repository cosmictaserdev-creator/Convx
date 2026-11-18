package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSmallcircleCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFSmallcircleCircleFill: ImageVector
    get() {
        if (_sFSmallcircleCircleFill != null) {
            return _sFSmallcircleCircleFill!!
        }
        _sFSmallcircleCircleFill = sfIcon(
            name = "Monochrome.SFSmallcircleCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM7.96875 12.7246C7.96875 15.3516 10.0879 17.4707 12.7148 17.4707C15.3418 17.4707 17.4707 15.3516 17.4707 12.7246C17.4707 10.0977 15.3418 7.96875 12.7148 7.96875C10.0879 7.96875 7.96875 10.0977 7.96875 12.7246ZM15.7031 12.7246C15.7031 14.375 14.375 15.7129 12.7148 15.7129C11.0645 15.7129 9.72656 14.375 9.72656 12.7246C9.72656 11.0645 11.0645 9.73633 12.7148 9.73633C14.375 9.73633 15.7031 11.0645 15.7031 12.7246Z", fillAlpha = 0.85f)
        }
        return _sFSmallcircleCircleFill!!
    }

private var _sFSmallcircleCircleFill: ImageVector? = null
