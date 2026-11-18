package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFStopCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFStopCircleFill: ImageVector
    get() {
        if (_sFStopCircleFill != null) {
            return _sFStopCircleFill!!
        }
        _sFStopCircleFill = sfIcon(
            name = "Monochrome.SFStopCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM9.27734 8.04688C8.50586 8.04688 8.03711 8.50586 8.03711 9.25781L8.03711 16.1816C8.03711 16.9434 8.50586 17.3926 9.27734 17.3926L16.1621 17.3926C16.9238 17.3926 17.3926 16.9434 17.3926 16.1816L17.3926 9.25781C17.3926 8.50586 16.9238 8.04688 16.1621 8.04688Z", fillAlpha = 0.85f)
        }
        return _sFStopCircleFill!!
    }

private var _sFStopCircleFill: ImageVector? = null
