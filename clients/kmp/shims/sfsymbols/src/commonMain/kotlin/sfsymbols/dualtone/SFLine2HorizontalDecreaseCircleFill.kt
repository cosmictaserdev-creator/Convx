package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLine2HorizontalDecreaseCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFLine2HorizontalDecreaseCircleFill: ImageVector
    get() {
        if (_sFLine2HorizontalDecreaseCircleFill != null) {
            return _sFLine2HorizontalDecreaseCircleFill!!
        }
        _sFLine2HorizontalDecreaseCircleFill = sfIcon(
            name = "Dualtone.SFLine2HorizontalDecreaseCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M5.625 11.8848C5.13672 11.8848 4.77539 11.5625 4.77539 11.0938C4.77539 10.6152 5.13672 10.3027 5.625 10.3027L19.8535 10.3027C20.332 10.3027 20.6934 10.6152 20.6934 11.0938C20.6934 11.5625 20.332 11.8848 19.8535 11.8848ZM7.5293 16.1523C7.03125 16.1523 6.67969 15.8398 6.67969 15.3613C6.67969 14.8926 7.03125 14.5703 7.5293 14.5703L17.9395 14.5703C18.4375 14.5703 18.7891 14.8926 18.7891 15.3613C18.7891 15.8398 18.4375 16.1523 17.9395 16.1523Z", fillAlpha = 0.85f)
        }
        return _sFLine2HorizontalDecreaseCircleFill!!
    }

private var _sFLine2HorizontalDecreaseCircleFill: ImageVector? = null
