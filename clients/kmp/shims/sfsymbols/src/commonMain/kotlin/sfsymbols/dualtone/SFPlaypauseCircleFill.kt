package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPlaypauseCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFPlaypauseCircleFill: ImageVector
    get() {
        if (_sFPlaypauseCircleFill != null) {
            return _sFPlaypauseCircleFill!!
        }
        _sFPlaypauseCircleFill = sfIcon(
            name = "Dualtone.SFPlaypauseCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M5.58594 17.627C5.04883 17.9688 4.4043 17.6855 4.4043 17.1191L4.4043 8.34961C4.4043 7.7832 5.07812 7.51953 5.58594 7.8418L12.2266 12.0996C12.7148 12.4219 12.7148 13.0566 12.2266 13.3789ZM14.4629 17.5684C13.8672 17.5684 13.6035 17.2461 13.6035 16.7773L13.6035 8.68164C13.6035 8.20312 13.8672 7.89062 14.4629 7.89062L15.7031 7.89062C16.2891 7.89062 16.5625 8.20312 16.5625 8.68164L16.5625 16.7773C16.5625 17.2461 16.2891 17.5684 15.7031 17.5684ZM18.9648 17.5684C18.3789 17.5684 18.0957 17.2461 18.0957 16.7773L18.0957 8.68164C18.0957 8.20312 18.3789 7.89062 18.9648 7.89062L20.2051 7.89062C20.7812 7.89062 21.0547 8.20312 21.0547 8.68164L21.0547 16.7773C21.0547 17.2461 20.7812 17.5684 20.2051 17.5684Z", fillAlpha = 0.85f)
        }
        return _sFPlaypauseCircleFill!!
    }

private var _sFPlaypauseCircleFill: ImageVector? = null
