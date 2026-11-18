package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFClockCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFClockCircleFill: ImageVector
    get() {
        if (_sFClockCircleFill != null) {
            return _sFClockCircleFill!!
        }
        _sFClockCircleFill = sfIcon(
            name = "Monochrome.SFClockCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM5.78125 12.7246C5.78125 16.5137 8.92578 19.6582 12.7148 19.6582C16.5039 19.6582 19.6484 16.5137 19.6484 12.7246C19.6484 8.93555 16.5039 5.78125 12.7051 5.78125C8.91602 5.78125 5.78125 8.93555 5.78125 12.7246ZM13.291 8.31055L13.291 13.0078C13.291 13.3301 13.0176 13.5645 12.7051 13.5645L9.0918 13.5645C8.75977 13.5645 8.52539 13.3398 8.52539 13.0078C8.52539 12.6855 8.75977 12.4414 9.0918 12.4414L12.1484 12.4414L12.1484 8.31055C12.1484 7.99805 12.3926 7.75391 12.7051 7.75391C13.0371 7.75391 13.291 7.99805 13.291 8.31055Z", fillAlpha = 0.85f)
        }
        return _sFClockCircleFill!!
    }

private var _sFClockCircleFill: ImageVector? = null
