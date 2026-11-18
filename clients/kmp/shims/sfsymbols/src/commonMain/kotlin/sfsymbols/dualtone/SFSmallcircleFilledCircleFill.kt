package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSmallcircleFilledCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFSmallcircleFilledCircleFill: ImageVector
    get() {
        if (_sFSmallcircleFilledCircleFill != null) {
            return _sFSmallcircleFilledCircleFill!!
        }
        _sFSmallcircleFilledCircleFill = sfIcon(
            name = "Dualtone.SFSmallcircleFilledCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M12.7148 16.7578C10.4883 16.7578 8.68164 14.9512 8.68164 12.7246C8.68164 10.4883 10.4883 8.68164 12.7148 8.68164C14.9512 8.68164 16.7578 10.4883 16.7578 12.7246C16.7578 14.9512 14.9512 16.7578 12.7148 16.7578Z", fillAlpha = 0.85f)
        }
        return _sFSmallcircleFilledCircleFill!!
    }

private var _sFSmallcircleFilledCircleFill: ImageVector? = null
