package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBackwardCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFBackwardCircleFill: ImageVector
    get() {
        if (_sFBackwardCircleFill != null) {
            return _sFBackwardCircleFill!!
        }
        _sFBackwardCircleFill = sfIcon(
            name = "Dualtone.SFBackwardCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M3.4668 12.7441C3.4668 12.4219 3.65234 12.1484 4.0332 11.9238L10.332 8.21289C10.5664 8.07617 10.7617 8.00781 10.9961 8.00781C11.4355 8.00781 11.8164 8.32031 11.8164 8.97461L11.8164 12.3926C11.9043 12.207 12.0508 12.0605 12.2852 11.9238L18.5938 8.21289C18.8184 8.07617 19.0234 8.00781 19.2578 8.00781C19.6875 8.00781 20.0781 8.32031 20.0781 8.97461L20.0781 16.5234C20.0781 17.168 19.6875 17.4805 19.2578 17.4805C19.0234 17.4805 18.8184 17.4219 18.5938 17.2852L12.2852 13.5645C12.0508 13.4375 11.9043 13.2812 11.8164 13.1055L11.8164 16.5234C11.8164 17.168 11.4355 17.4805 10.9961 17.4805C10.7617 17.4805 10.5664 17.4219 10.332 17.2852L4.0332 13.5645C3.65234 13.3496 3.4668 13.0664 3.4668 12.7441Z", fillAlpha = 0.85f)
        }
        return _sFBackwardCircleFill!!
    }

private var _sFBackwardCircleFill: ImageVector? = null
