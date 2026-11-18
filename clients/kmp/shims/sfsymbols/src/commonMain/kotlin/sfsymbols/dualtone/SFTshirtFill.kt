package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTshirtFill (dualtone)
 * Viewport: 33.0821 x 27.0605
 */
public val SfSymbols.Dualtone.SFTshirtFill: ImageVector
    get() {
        if (_sFTshirtFill != null) {
            return _sFTshirtFill!!
        }
        _sFTshirtFill = sfIcon(
            name = "Dualtone.SFTshirtFill",
            viewportWidth = 33.0821f,
            viewportHeight = 27.0605f
        ) {
            addSfPath("M10.0176 27.0605L22.7031 27.0605C24.9492 27.0605 26.0332 25.9961 26.0723 23.7695L26.2285 11.3477L29.4317 12.0703C30.5254 12.3145 31.2188 11.875 31.502 10.752L32.6348 6.40625C32.9278 5.29297 32.4981 4.51172 31.4238 4.05273L22.5957 0.214844C22.1172 0 21.6973 0.0195312 21.2676 0.234375C19.6856 0.996094 18.2207 1.41602 16.3653 1.41602C14.5 1.41602 13.0352 0.996094 11.4629 0.234375C11.0235 0.0195312 10.6035 0 10.125 0.214844L1.29689 4.05273C0.222673 4.51172-0.197249 5.29297 0.085954 6.40625L1.21877 10.752C1.51174 11.875 2.20509 12.3145 3.28908 12.0703L6.4922 11.3477L6.65822 23.7695C6.68752 25.9961 7.7715 27.0605 10.0176 27.0605ZM16.3653 5.27344C14.1875 5.27344 12.5469 4.05273 11.961 2.10938C13.3281 2.64648 14.8711 2.94922 16.3653 2.94922C17.8496 2.94922 19.3926 2.64648 20.7598 2.10938C20.1934 4.0332 18.5625 5.27344 16.3653 5.27344Z", fillAlpha = 0.85f)
        }
        return _sFTshirtFill!!
    }

private var _sFTshirtFill: ImageVector? = null
