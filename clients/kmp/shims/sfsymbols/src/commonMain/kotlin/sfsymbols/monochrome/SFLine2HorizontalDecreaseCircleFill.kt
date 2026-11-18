package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLine2HorizontalDecreaseCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFLine2HorizontalDecreaseCircleFill: ImageVector
    get() {
        if (_sFLine2HorizontalDecreaseCircleFill != null) {
            return _sFLine2HorizontalDecreaseCircleFill!!
        }
        _sFLine2HorizontalDecreaseCircleFill = sfIcon(
            name = "Monochrome.SFLine2HorizontalDecreaseCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM7.5293 14.5703C7.03125 14.5703 6.67969 14.8926 6.67969 15.3613C6.67969 15.8398 7.03125 16.1523 7.5293 16.1523L17.9395 16.1523C18.4375 16.1523 18.7891 15.8398 18.7891 15.3613C18.7891 14.8926 18.4375 14.5703 17.9395 14.5703ZM5.625 10.3027C5.13672 10.3027 4.77539 10.6152 4.77539 11.0938C4.77539 11.5625 5.13672 11.8848 5.625 11.8848L19.8535 11.8848C20.332 11.8848 20.6934 11.5625 20.6934 11.0938C20.6934 10.6152 20.332 10.3027 19.8535 10.3027Z", fillAlpha = 0.85f)
        }
        return _sFLine2HorizontalDecreaseCircleFill!!
    }

private var _sFLine2HorizontalDecreaseCircleFill: ImageVector? = null
