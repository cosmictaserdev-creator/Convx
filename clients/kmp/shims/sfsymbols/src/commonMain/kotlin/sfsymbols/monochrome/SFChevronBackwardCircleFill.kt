package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFChevronBackwardCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFChevronBackwardCircleFill: ImageVector
    get() {
        if (_sFChevronBackwardCircleFill != null) {
            return _sFChevronBackwardCircleFill!!
        }
        _sFChevronBackwardCircleFill = sfIcon(
            name = "Monochrome.SFChevronBackwardCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM14.0137 6.55273L8.63281 11.6699C8.01758 12.2461 8.02734 13.2129 8.63281 13.7793L14.0137 18.8965C14.3164 19.1992 14.8828 19.209 15.166 18.9258C15.498 18.6035 15.4883 18.0762 15.166 17.7637L9.86328 12.7148L15.166 7.67578C15.498 7.37305 15.4883 6.83594 15.1562 6.52344C14.8438 6.2207 14.3457 6.23047 14.0137 6.55273Z", fillAlpha = 0.85f)
        }
        return _sFChevronBackwardCircleFill!!
    }

private var _sFChevronBackwardCircleFill: ImageVector? = null
