package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPiCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFPiCircleFill: ImageVector
    get() {
        if (_sFPiCircleFill != null) {
            return _sFPiCircleFill!!
        }
        _sFPiCircleFill = sfIcon(
            name = "Monochrome.SFPiCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM6.95312 6.99219C6.50391 6.99219 6.18164 7.29492 6.18164 7.76367C6.18164 8.22266 6.50391 8.52539 6.95312 8.52539L8.62305 8.52539L8.62305 17.959C8.62305 18.4473 8.97461 18.7598 9.46289 18.7598C9.95117 18.7598 10.293 18.4473 10.293 17.959L10.293 8.52539L14.9805 8.52539L14.9805 16.0742C14.9805 17.793 15.7227 18.7109 17.334 18.7109C18.2617 18.7109 18.7988 18.4082 18.7988 17.8418C18.7988 17.3828 18.5254 17.1387 17.998 17.1387C17.8613 17.1387 17.6953 17.168 17.5391 17.168C16.9434 17.168 16.6406 16.8359 16.6406 16.0254L16.6406 8.52539L18.3691 8.52539C18.8086 8.52539 19.1309 8.22266 19.1309 7.76367C19.1309 7.29492 18.8086 6.99219 18.3691 6.99219Z", fillAlpha = 0.85f)
        }
        return _sFPiCircleFill!!
    }

private var _sFPiCircleFill: ImageVector? = null
