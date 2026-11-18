package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBackwardFrameFill (monochrome)
 * Viewport: 31.1035 x 18.9746
 */
public val SfSymbols.Monochrome.SFBackwardFrameFill: ImageVector
    get() {
        if (_sFBackwardFrameFill != null) {
            return _sFBackwardFrameFill!!
        }
        _sFBackwardFrameFill = sfIcon(
            name = "Monochrome.SFBackwardFrameFill",
            viewportWidth = 31.1035f,
            viewportHeight = 18.9746f
        ) {
            addSfPath("M25.127 18.8965C26.1621 18.8965 26.6797 18.3789 26.6797 17.334L26.6797 1.61133C26.6797 0.576172 26.1621 0.0488281 25.127 0.0488281L22.3047 0.0488281C21.2695 0.0488281 20.752 0.527344 20.752 1.61133L20.752 17.334C20.752 18.3789 21.2695 18.8965 22.3047 18.8965ZM16.7676 17.3633L16.7676 1.60156C16.7676 0.507812 16.1328 0 15.3809 0C15.0586 0 14.7168 0.0976562 14.3945 0.283203L1.16211 8.04688C0.332031 8.52539 0 8.90625 0 9.48242C0 10.0586 0.332031 10.4297 1.16211 10.9082L14.3945 18.6719C14.7168 18.8574 15.0586 18.9551 15.3809 18.9551C16.1328 18.9551 16.7676 18.457 16.7676 17.3633Z", fillAlpha = 0.85f)
        }
        return _sFBackwardFrameFill!!
    }

private var _sFBackwardFrameFill: ImageVector? = null
