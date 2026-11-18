package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBackwardEndFill (monochrome)
 * Viewport: 23.0078 x 18.9746
 */
public val SfSymbols.Monochrome.SFBackwardEndFill: ImageVector
    get() {
        if (_sFBackwardEndFill != null) {
            return _sFBackwardEndFill!!
        }
        _sFBackwardEndFill = sfIcon(
            name = "Monochrome.SFBackwardEndFill",
            viewportWidth = 23.0078f,
            viewportHeight = 18.9746f
        ) {
            addSfPath("M22.6465 17.3633L22.6465 1.60156C22.6465 0.507812 22.002 0 21.2598 0C20.9277 0 20.5957 0.0976562 20.2637 0.283203L7.04102 8.04688C6.21094 8.52539 5.86914 8.90625 5.86914 9.48242C5.86914 10.0586 6.21094 10.4297 7.04102 10.9082L20.2637 18.6719C20.5957 18.8574 20.9277 18.9551 21.2598 18.9551C22.002 18.9551 22.6465 18.457 22.6465 17.3633ZM4.375 18.8965C5.41992 18.8965 5.9375 18.3789 5.9375 17.334L5.9375 1.61133C5.9375 0.576172 5.41992 0.0488281 4.375 0.0488281L1.5625 0.0488281C0.517578 0.0488281 0 0.527344 0 1.61133L0 17.334C0 18.3789 0.517578 18.8965 1.5625 18.8965Z", fillAlpha = 0.85f)
        }
        return _sFBackwardEndFill!!
    }

private var _sFBackwardEndFill: ImageVector? = null
