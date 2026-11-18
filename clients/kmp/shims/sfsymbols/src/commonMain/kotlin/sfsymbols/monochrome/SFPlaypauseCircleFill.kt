package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPlaypauseCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFPlaypauseCircleFill: ImageVector
    get() {
        if (_sFPlaypauseCircleFill != null) {
            return _sFPlaypauseCircleFill!!
        }
        _sFPlaypauseCircleFill = sfIcon(
            name = "Monochrome.SFPlaypauseCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM4.4043 8.34961L4.4043 17.1191C4.4043 17.6855 5.04883 17.9688 5.58594 17.627L12.2266 13.3789C12.7148 13.0566 12.7148 12.4219 12.2266 12.0996L5.58594 7.8418C5.07812 7.51953 4.4043 7.7832 4.4043 8.34961ZM14.4629 7.89062C13.8672 7.89062 13.6035 8.20312 13.6035 8.68164L13.6035 16.7773C13.6035 17.2461 13.8672 17.5684 14.4629 17.5684L15.7031 17.5684C16.2891 17.5684 16.5625 17.2461 16.5625 16.7773L16.5625 8.68164C16.5625 8.20312 16.2891 7.89062 15.7031 7.89062ZM18.9648 7.89062C18.3789 7.89062 18.0957 8.20312 18.0957 8.68164L18.0957 16.7773C18.0957 17.2461 18.3789 17.5684 18.9648 17.5684L20.2051 17.5684C20.7812 17.5684 21.0547 17.2461 21.0547 16.7773L21.0547 8.68164C21.0547 8.20312 20.7812 7.89062 20.2051 7.89062Z", fillAlpha = 0.85f)
        }
        return _sFPlaypauseCircleFill!!
    }

private var _sFPlaypauseCircleFill: ImageVector? = null
