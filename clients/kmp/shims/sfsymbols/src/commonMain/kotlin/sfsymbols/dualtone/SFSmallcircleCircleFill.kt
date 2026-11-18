package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSmallcircleCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFSmallcircleCircleFill: ImageVector
    get() {
        if (_sFSmallcircleCircleFill != null) {
            return _sFSmallcircleCircleFill!!
        }
        _sFSmallcircleCircleFill = sfIcon(
            name = "Dualtone.SFSmallcircleCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M12.7148 17.4707C10.0879 17.4707 7.96875 15.3516 7.96875 12.7246C7.96875 10.0977 10.0879 7.96875 12.7148 7.96875C15.3418 7.96875 17.4707 10.0977 17.4707 12.7246C17.4707 15.3516 15.3418 17.4707 12.7148 17.4707ZM12.7148 15.7129C14.375 15.7129 15.7031 14.375 15.7031 12.7246C15.7031 11.0645 14.375 9.73633 12.7148 9.73633C11.0645 9.73633 9.72656 11.0645 9.72656 12.7246C9.72656 14.375 11.0645 15.7129 12.7148 15.7129Z", fillAlpha = 0.85f)
        }
        return _sFSmallcircleCircleFill!!
    }

private var _sFSmallcircleCircleFill: ImageVector? = null
