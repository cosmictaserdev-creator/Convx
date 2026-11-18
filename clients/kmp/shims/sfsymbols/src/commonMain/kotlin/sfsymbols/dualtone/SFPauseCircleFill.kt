package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPauseCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFPauseCircleFill: ImageVector
    get() {
        if (_sFPauseCircleFill != null) {
            return _sFPauseCircleFill!!
        }
        _sFPauseCircleFill = sfIcon(
            name = "Dualtone.SFPauseCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M9.11133 17.7051C8.49609 17.7051 8.22266 17.373 8.22266 16.8945L8.22266 8.53516C8.22266 8.05664 8.49609 7.72461 9.11133 7.72461L10.3906 7.72461C11.0059 7.72461 11.2793 8.05664 11.2793 8.53516L11.2793 16.8945C11.2793 17.373 11.0059 17.7051 10.3906 17.7051ZM15.0586 17.7051C14.4434 17.7051 14.1602 17.373 14.1602 16.8945L14.1602 8.53516C14.1602 8.05664 14.4434 7.72461 15.0586 7.72461L16.3379 7.72461C16.9336 7.72461 17.2168 8.05664 17.2168 8.53516L17.2168 16.8945C17.2168 17.373 16.9336 17.7051 16.3379 17.7051Z", fillAlpha = 0.85f)
        }
        return _sFPauseCircleFill!!
    }

private var _sFPauseCircleFill: ImageVector? = null
