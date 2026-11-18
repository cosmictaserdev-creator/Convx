package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPlaypauseFill (monochrome)
 * Viewport: 36.0742 x 18.9746
 */
public val SfSymbols.Monochrome.SFPlaypauseFill: ImageVector
    get() {
        if (_sFPlaypauseFill != null) {
            return _sFPlaypauseFill!!
        }
        _sFPlaypauseFill = sfIcon(
            name = "Monochrome.SFPlaypauseFill",
            viewportWidth = 36.0742f,
            viewportHeight = 18.9746f
        ) {
            addSfPath("M0 17.3633C0 18.457 0.644531 18.9551 1.38672 18.9551C1.71875 18.9551 2.05078 18.8574 2.37305 18.6719L15.6055 10.9082C16.4355 10.4297 16.7676 10.0586 16.7676 9.48242C16.7676 8.90625 16.4355 8.52539 15.6055 8.04688L2.37305 0.283203C2.05078 0.0976562 1.71875 0 1.38672 0C0.644531 0 0 0.507812 0 1.60156ZM22.4414 18.8965L25.2539 18.8965C26.2988 18.8965 26.8164 18.3789 26.8164 17.334L26.8164 1.61133C26.8164 0.527344 26.2988 0.0488281 25.2539 0.0488281L22.4414 0.0488281C21.3965 0.0488281 20.8789 0.576172 20.8789 1.61133L20.8789 17.334C20.8789 18.3789 21.3965 18.8965 22.4414 18.8965ZM31.3379 18.8965L34.1504 18.8965C35.1953 18.8965 35.7129 18.3789 35.7129 17.334L35.7129 1.61133C35.7129 0.527344 35.1953 0.0488281 34.1504 0.0488281L31.3379 0.0488281C30.293 0.0488281 29.7754 0.576172 29.7754 1.61133L29.7754 17.334C29.7754 18.3789 30.293 18.8965 31.3379 18.8965Z", fillAlpha = 0.85f)
        }
        return _sFPlaypauseFill!!
    }

private var _sFPlaypauseFill: ImageVector? = null
