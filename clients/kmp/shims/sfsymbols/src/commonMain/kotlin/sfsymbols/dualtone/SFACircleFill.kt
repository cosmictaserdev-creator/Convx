package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFACircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFACircleFill: ImageVector
    get() {
        if (_sFACircleFill != null) {
            return _sFACircleFill!!
        }
        _sFACircleFill = sfIcon(
            name = "Dualtone.SFACircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M8.47656 18.7109C7.95898 18.7109 7.63672 18.3984 7.63672 17.9395C7.63672 17.7734 7.66602 17.6172 7.75391 17.373L11.4746 7.43164C11.6895 6.8457 12.0996 6.55273 12.7051 6.55273C13.3008 6.55273 13.7305 6.8457 13.9453 7.43164L17.666 17.373C17.7539 17.6172 17.7832 17.7734 17.7832 17.9492C17.7832 18.3984 17.4414 18.7109 16.9531 18.7109C16.5332 18.7109 16.2598 18.5254 16.084 17.998L15.0586 15.0977L10.3516 15.0977L9.33594 17.998C9.16016 18.5254 8.87695 18.7109 8.47656 18.7109ZM10.8105 13.7305L14.5996 13.7305L12.7637 8.52539L12.6465 8.52539Z", fillAlpha = 0.85f)
        }
        return _sFACircleFill!!
    }

private var _sFACircleFill: ImageVector? = null
