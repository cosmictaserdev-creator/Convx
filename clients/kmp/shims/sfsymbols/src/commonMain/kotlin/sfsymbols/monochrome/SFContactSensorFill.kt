package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFContactSensorFill (monochrome)
 * Viewport: 22.0801 x 32.8027
 */
public val SfSymbols.Monochrome.SFContactSensorFill: ImageVector
    get() {
        if (_sFContactSensorFill != null) {
            return _sFContactSensorFill!!
        }
        _sFContactSensorFill = sfIcon(
            name = "Monochrome.SFContactSensorFill",
            viewportWidth = 22.0801f,
            viewportHeight = 32.8027f
        ) {
            addSfPath("M3.79883 27.2266L9.9707 27.2266L9.9707 5.55664L3.79883 5.55664C1.2793 5.55664 0 6.82617 0 9.31641L0 23.4668C0 25.9668 1.2793 27.2266 3.79883 27.2266ZM17.9199 27.2266C20.4395 27.2266 21.7188 25.9473 21.7188 23.4668L21.7188 9.31641C21.7188 6.83594 20.4395 5.55664 17.9199 5.55664L11.7285 5.55664L11.7285 27.2266ZM10.8496 0C10.3613 0 9.9707 0.390625 9.9707 0.878906L9.9707 5.55664L11.7285 5.55664L11.7285 0.878906C11.7285 0.390625 11.3379 0 10.8496 0ZM10.8496 32.7832C11.3379 32.7832 11.7285 32.3926 11.7285 31.9043L11.7285 27.2266L9.9707 27.2266L9.9707 31.9043C9.9707 32.3926 10.3613 32.7832 10.8496 32.7832Z", fillAlpha = 0.85f)
        }
        return _sFContactSensorFill!!
    }

private var _sFContactSensorFill: ImageVector? = null
