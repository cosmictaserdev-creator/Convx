package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDiamondCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFDiamondCircleFill: ImageVector
    get() {
        if (_sFDiamondCircleFill != null) {
            return _sFDiamondCircleFill!!
        }
        _sFDiamondCircleFill = sfIcon(
            name = "Monochrome.SFDiamondCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM11.6895 6.10352L6.10352 11.6895C5.41992 12.373 5.44922 13.0859 6.12305 13.7598L11.6797 19.3262C12.3633 20.0098 13.0664 20.0293 13.75 19.3457L19.3359 13.7598C20.0293 13.0664 20 12.3633 19.3164 11.6797L13.7598 6.12305C13.0762 5.43945 12.373 5.41992 11.6895 6.10352Z", fillAlpha = 0.85f)
        }
        return _sFDiamondCircleFill!!
    }

private var _sFDiamondCircleFill: ImageVector? = null
