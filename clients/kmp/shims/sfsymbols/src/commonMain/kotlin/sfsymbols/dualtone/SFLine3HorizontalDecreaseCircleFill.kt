package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLine3HorizontalDecreaseCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFLine3HorizontalDecreaseCircleFill: ImageVector
    get() {
        if (_sFLine3HorizontalDecreaseCircleFill != null) {
            return _sFLine3HorizontalDecreaseCircleFill!!
        }
        _sFLine3HorizontalDecreaseCircleFill = sfIcon(
            name = "Dualtone.SFLine3HorizontalDecreaseCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M5.625 9.99023C5.13672 9.99023 4.77539 9.67773 4.77539 9.19922C4.77539 8.7207 5.13672 8.4082 5.625 8.4082L19.8535 8.4082C20.332 8.4082 20.6934 8.7207 20.6934 9.19922C20.6934 9.67773 20.332 9.99023 19.8535 9.99023ZM7.5293 14.2578C7.03125 14.2578 6.67969 13.9453 6.67969 13.4668C6.67969 12.9883 7.03125 12.666 7.5293 12.666L17.9395 12.666C18.4375 12.666 18.7891 12.9883 18.7891 13.4668C18.7891 13.9453 18.4375 14.2578 17.9395 14.2578ZM9.52148 18.5449C9.02344 18.5449 8.67188 18.2227 8.67188 17.7441C8.67188 17.2754 9.02344 16.9629 9.52148 16.9629L15.957 16.9629C16.4453 16.9629 16.7969 17.2754 16.7969 17.7441C16.7969 18.2227 16.4453 18.5449 15.957 18.5449Z", fillAlpha = 0.85f)
        }
        return _sFLine3HorizontalDecreaseCircleFill!!
    }

private var _sFLine3HorizontalDecreaseCircleFill: ImageVector? = null
