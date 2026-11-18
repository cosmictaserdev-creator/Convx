package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SF7Calendar (dualtone)
 * Viewport: 25.3809 x 22.9785
 */
public val SfSymbols.Dualtone.SF7Calendar: ImageVector
    get() {
        if (_sF7Calendar != null) {
            return _sF7Calendar!!
        }
        _sF7Calendar = sfIcon(
            name = "Dualtone.SF7Calendar",
            viewportWidth = 25.3809f,
            viewportHeight = 22.9785f
        ) {
            addSfPath("M3.79883 22.9785L21.2109 22.9785C23.7402 22.9785 25.0195 21.709 25.0195 19.2188L25.0195 3.78906C25.0195 1.29883 23.7402 0.0292969 21.2109 0.0292969L3.79883 0.0292969C1.2793 0.0292969 0 1.28906 0 3.78906L0 19.2188C0 21.7188 1.2793 22.9785 3.79883 22.9785ZM3.66211 21.25C2.41211 21.25 1.72852 20.5859 1.72852 19.2969L1.72852 7.64648C1.72852 6.34766 2.41211 5.69336 3.66211 5.69336L21.3379 5.69336C22.5781 5.69336 23.2812 6.34766 23.2812 7.64648L23.2812 19.2969C23.2812 20.5859 22.5781 21.25 21.3379 21.25Z", fillAlpha = 0.425f)
            addSfPath("M10.9473 19.0137C11.2988 19.0137 11.5332 18.8574 11.748 18.457L16.2207 9.94141C16.416 9.57031 16.4648 9.36523 16.4648 9.13086C16.4648 8.56445 16.0156 8.14453 15.332 8.14453L9.54102 8.14453C9.17969 8.14453 8.92578 8.4082 8.92578 8.7793C8.92578 9.15039 9.18945 9.41406 9.54102 9.41406L14.7168 9.41406L14.7168 9.50195L10.2832 17.7539C10.1758 17.9395 10.1367 18.0859 10.1367 18.291C10.1367 18.7207 10.498 19.0137 10.9473 19.0137Z", fillAlpha = 0.85f)
        }
        return _sF7Calendar!!
    }

private var _sF7Calendar: ImageVector? = null
