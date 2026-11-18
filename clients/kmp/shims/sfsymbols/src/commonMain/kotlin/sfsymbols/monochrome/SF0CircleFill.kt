package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SF0CircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SF0CircleFill: ImageVector
    get() {
        if (_sF0CircleFill != null) {
            return _sF0CircleFill!!
        }
        _sF0CircleFill = sfIcon(
            name = "Monochrome.SF0CircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM8.28125 12.7246C8.28125 16.3574 10.0586 18.8086 12.7148 18.8086C15.3711 18.8086 17.1582 16.3574 17.1582 12.7246C17.1582 9.08203 15.3711 6.64062 12.7148 6.64062C10.0586 6.64062 8.28125 9.08203 8.28125 12.7246ZM15.4395 12.7246C15.4395 15.4883 14.3359 17.3535 12.7148 17.3535C11.0938 17.3535 9.99023 15.4883 9.99023 12.7246C9.99023 9.95117 11.0938 8.08594 12.7148 8.08594C14.3359 8.08594 15.4395 9.95117 15.4395 12.7246Z", fillAlpha = 0.85f)
        }
        return _sF0CircleFill!!
    }

private var _sF0CircleFill: ImageVector? = null
