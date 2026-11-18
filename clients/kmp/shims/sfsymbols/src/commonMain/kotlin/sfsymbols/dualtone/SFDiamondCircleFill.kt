package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDiamondCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFDiamondCircleFill: ImageVector
    get() {
        if (_sFDiamondCircleFill != null) {
            return _sFDiamondCircleFill!!
        }
        _sFDiamondCircleFill = sfIcon(
            name = "Dualtone.SFDiamondCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M6.12305 13.7598C5.44922 13.0859 5.41992 12.373 6.10352 11.6895L11.6895 6.10352C12.373 5.41992 13.0762 5.43945 13.7598 6.12305L19.3164 11.6797C20 12.3633 20.0293 13.0664 19.3359 13.7598L13.75 19.3457C13.0664 20.0293 12.3633 20.0098 11.6797 19.3262Z", fillAlpha = 0.85f)
        }
        return _sFDiamondCircleFill!!
    }

private var _sFDiamondCircleFill: ImageVector? = null
