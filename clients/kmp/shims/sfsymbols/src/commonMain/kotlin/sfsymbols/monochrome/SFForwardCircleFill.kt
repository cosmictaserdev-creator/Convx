package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFForwardCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFForwardCircleFill: ImageVector
    get() {
        if (_sFForwardCircleFill != null) {
            return _sFForwardCircleFill!!
        }
        _sFForwardCircleFill = sfIcon(
            name = "Monochrome.SFForwardCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM13.6426 8.97461L13.6426 12.3926C13.5547 12.207 13.3984 12.0605 13.1738 11.9238L6.86523 8.21289C6.64062 8.07617 6.42578 8.00781 6.20117 8.00781C5.76172 8.00781 5.38086 8.32031 5.38086 8.97461L5.38086 16.5234C5.38086 17.168 5.76172 17.4805 6.20117 17.4805C6.42578 17.4805 6.64062 17.4219 6.86523 17.2852L13.1738 13.5645C13.3984 13.4375 13.5547 13.2812 13.6426 13.1055L13.6426 16.5234C13.6426 17.168 14.0137 17.4805 14.4531 17.4805C14.6875 17.4805 14.8926 17.4219 15.1172 17.2852L21.416 13.5645C21.7969 13.3496 21.9824 13.0664 21.9824 12.7441C21.9824 12.4219 21.7969 12.1484 21.416 11.9238L15.1172 8.21289C14.8926 8.07617 14.6875 8.00781 14.4531 8.00781C14.0137 8.00781 13.6426 8.32031 13.6426 8.97461Z", fillAlpha = 0.85f)
        }
        return _sFForwardCircleFill!!
    }

private var _sFForwardCircleFill: ImageVector? = null
