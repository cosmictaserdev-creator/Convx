package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFStopCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFStopCircleFill: ImageVector
    get() {
        if (_sFStopCircleFill != null) {
            return _sFStopCircleFill!!
        }
        _sFStopCircleFill = sfIcon(
            name = "Dualtone.SFStopCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M9.27734 17.3926C8.50586 17.3926 8.03711 16.9434 8.03711 16.1816L8.03711 9.25781C8.03711 8.50586 8.50586 8.04688 9.27734 8.04688L16.1621 8.04688C16.9238 8.04688 17.3926 8.50586 17.3926 9.25781L17.3926 16.1816C17.3926 16.9434 16.9238 17.3926 16.1621 17.3926Z", fillAlpha = 0.85f)
        }
        return _sFStopCircleFill!!
    }

private var _sFStopCircleFill: ImageVector? = null
