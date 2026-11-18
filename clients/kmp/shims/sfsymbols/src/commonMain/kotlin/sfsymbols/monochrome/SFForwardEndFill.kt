package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFForwardEndFill (monochrome)
 * Viewport: 23.0078 x 18.9746
 */
public val SfSymbols.Monochrome.SFForwardEndFill: ImageVector
    get() {
        if (_sFForwardEndFill != null) {
            return _sFForwardEndFill!!
        }
        _sFForwardEndFill = sfIcon(
            name = "Monochrome.SFForwardEndFill",
            viewportWidth = 23.0078f,
            viewportHeight = 18.9746f
        ) {
            addSfPath("M0 17.3633C0 18.457 0.644531 18.9551 1.38672 18.9551C1.71875 18.9551 2.05078 18.8574 2.37305 18.6719L15.6055 10.9082C16.4355 10.4297 16.7676 10.0586 16.7676 9.48242C16.7676 8.90625 16.4355 8.52539 15.6055 8.04688L2.37305 0.283203C2.05078 0.0976562 1.71875 0 1.38672 0C0.644531 0 0 0.507812 0 1.60156ZM18.2715 18.8965L21.084 18.8965C22.1289 18.8965 22.6465 18.3789 22.6465 17.334L22.6465 1.61133C22.6465 0.527344 22.1289 0.0488281 21.084 0.0488281L18.2715 0.0488281C17.2266 0.0488281 16.709 0.576172 16.709 1.61133L16.709 17.334C16.709 18.3789 17.2266 18.8965 18.2715 18.8965Z", fillAlpha = 0.85f)
        }
        return _sFForwardEndFill!!
    }

private var _sFForwardEndFill: ImageVector? = null
