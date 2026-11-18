package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFChevronBackwardCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFChevronBackwardCircleFill: ImageVector
    get() {
        if (_sFChevronBackwardCircleFill != null) {
            return _sFChevronBackwardCircleFill!!
        }
        _sFChevronBackwardCircleFill = sfIcon(
            name = "Dualtone.SFChevronBackwardCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M15.166 18.9258C14.8828 19.209 14.3164 19.1992 14.0137 18.8965L8.63281 13.7793C8.02734 13.2129 8.01758 12.2461 8.63281 11.6699L14.0137 6.55273C14.3457 6.23047 14.8438 6.2207 15.1562 6.52344C15.4883 6.83594 15.498 7.37305 15.166 7.67578L9.86328 12.7148L15.166 17.7637C15.4883 18.0762 15.498 18.6035 15.166 18.9258Z", fillAlpha = 0.85f)
        }
        return _sFChevronBackwardCircleFill!!
    }

private var _sFChevronBackwardCircleFill: ImageVector? = null
