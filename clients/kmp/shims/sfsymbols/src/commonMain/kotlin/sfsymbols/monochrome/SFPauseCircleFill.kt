package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPauseCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFPauseCircleFill: ImageVector
    get() {
        if (_sFPauseCircleFill != null) {
            return _sFPauseCircleFill!!
        }
        _sFPauseCircleFill = sfIcon(
            name = "Monochrome.SFPauseCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM9.11133 7.72461C8.49609 7.72461 8.22266 8.05664 8.22266 8.53516L8.22266 16.8945C8.22266 17.373 8.49609 17.7051 9.11133 17.7051L10.3906 17.7051C11.0059 17.7051 11.2793 17.373 11.2793 16.8945L11.2793 8.53516C11.2793 8.05664 11.0059 7.72461 10.3906 7.72461ZM15.0586 7.72461C14.4434 7.72461 14.1602 8.05664 14.1602 8.53516L14.1602 16.8945C14.1602 17.373 14.4434 17.7051 15.0586 17.7051L16.3379 17.7051C16.9336 17.7051 17.2168 17.373 17.2168 16.8945L17.2168 8.53516C17.2168 8.05664 16.9336 7.72461 16.3379 7.72461Z", fillAlpha = 0.85f)
        }
        return _sFPauseCircleFill!!
    }

private var _sFPauseCircleFill: ImageVector? = null
