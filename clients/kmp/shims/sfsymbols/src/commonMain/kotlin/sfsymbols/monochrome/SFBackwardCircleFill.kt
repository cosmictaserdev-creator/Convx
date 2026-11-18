package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBackwardCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFBackwardCircleFill: ImageVector
    get() {
        if (_sFBackwardCircleFill != null) {
            return _sFBackwardCircleFill!!
        }
        _sFBackwardCircleFill = sfIcon(
            name = "Monochrome.SFBackwardCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM18.5938 8.21289L12.2852 11.9238C12.0508 12.0605 11.9043 12.207 11.8164 12.3926L11.8164 8.97461C11.8164 8.32031 11.4355 8.00781 10.9961 8.00781C10.7617 8.00781 10.5664 8.07617 10.332 8.21289L4.0332 11.9238C3.65234 12.1484 3.4668 12.4219 3.4668 12.7441C3.4668 13.0664 3.65234 13.3496 4.0332 13.5645L10.332 17.2852C10.5664 17.4219 10.7617 17.4805 10.9961 17.4805C11.4355 17.4805 11.8164 17.168 11.8164 16.5234L11.8164 13.1055C11.9043 13.2812 12.0508 13.4375 12.2852 13.5645L18.5938 17.2852C18.8184 17.4219 19.0234 17.4805 19.2578 17.4805C19.6875 17.4805 20.0781 17.168 20.0781 16.5234L20.0781 8.97461C20.0781 8.32031 19.6875 8.00781 19.2578 8.00781C19.0234 8.00781 18.8184 8.07617 18.5938 8.21289Z", fillAlpha = 0.85f)
        }
        return _sFBackwardCircleFill!!
    }

private var _sFBackwardCircleFill: ImageVector? = null
