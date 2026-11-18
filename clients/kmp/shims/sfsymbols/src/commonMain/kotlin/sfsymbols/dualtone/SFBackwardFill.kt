package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBackwardFill (dualtone)
 * Viewport: 36.4648 x 18.9746
 */
public val SfSymbols.Dualtone.SFBackwardFill: ImageVector
    get() {
        if (_sFBackwardFill != null) {
            return _sFBackwardFill!!
        }
        _sFBackwardFill = sfIcon(
            name = "Dualtone.SFBackwardFill",
            viewportWidth = 36.4648f,
            viewportHeight = 18.9746f
        ) {
            addSfPath("M33.4863 17.3633L33.4863 1.60156C33.4863 0.507812 32.8418 0 32.0996 0C31.7676 0 31.4355 0.0976562 31.1035 0.283203L17.8711 8.04688C17.041 8.52539 16.709 8.90625 16.709 9.48242C16.709 10.0586 17.041 10.4297 17.8711 10.9082L31.1035 18.6719C31.4355 18.8574 31.7676 18.9551 32.0996 18.9551C32.8418 18.9551 33.4863 18.457 33.4863 17.3633ZM16.7773 17.3633L16.7773 1.60156C16.7773 0.507812 16.1328 0 15.3906 0C15.0586 0 14.7266 0.0976562 14.3945 0.283203L1.17188 8.04688C0.341797 8.52539 0 8.90625 0 9.48242C0 10.0586 0.341797 10.4297 1.17188 10.9082L14.3945 18.6719C14.7266 18.8574 15.0586 18.9551 15.3906 18.9551C16.1328 18.9551 16.7773 18.457 16.7773 17.3633Z", fillAlpha = 0.85f)
        }
        return _sFBackwardFill!!
    }

private var _sFBackwardFill: ImageVector? = null
