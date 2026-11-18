package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPiCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFPiCircleFill: ImageVector
    get() {
        if (_sFPiCircleFill != null) {
            return _sFPiCircleFill!!
        }
        _sFPiCircleFill = sfIcon(
            name = "Dualtone.SFPiCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M9.46289 18.7598C8.97461 18.7598 8.62305 18.4473 8.62305 17.959L8.62305 8.52539L6.95312 8.52539C6.50391 8.52539 6.18164 8.22266 6.18164 7.76367C6.18164 7.29492 6.50391 6.99219 6.95312 6.99219L18.3691 6.99219C18.8086 6.99219 19.1309 7.29492 19.1309 7.76367C19.1309 8.22266 18.8086 8.52539 18.3691 8.52539L16.6406 8.52539L16.6406 16.0254C16.6406 16.8359 16.9434 17.168 17.5391 17.168C17.6953 17.168 17.8613 17.1387 17.998 17.1387C18.5254 17.1387 18.7988 17.3828 18.7988 17.8418C18.7988 18.4082 18.2617 18.7109 17.334 18.7109C15.7227 18.7109 14.9805 17.793 14.9805 16.0742L14.9805 8.52539L10.293 8.52539L10.293 17.959C10.293 18.4473 9.95117 18.7598 9.46289 18.7598Z", fillAlpha = 0.85f)
        }
        return _sFPiCircleFill!!
    }

private var _sFPiCircleFill: ImageVector? = null
