package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SF1Calendar (dualtone)
 * Viewport: 25.3809 x 22.9785
 */
public val SfSymbols.Dualtone.SF1Calendar: ImageVector
    get() {
        if (_sF1Calendar != null) {
            return _sF1Calendar!!
        }
        _sF1Calendar = sfIcon(
            name = "Dualtone.SF1Calendar",
            viewportWidth = 25.3809f,
            viewportHeight = 22.9785f
        ) {
            addSfPath("M3.79883 22.9785L21.2109 22.9785C23.7402 22.9785 25.0195 21.709 25.0195 19.2188L25.0195 3.78906C25.0195 1.29883 23.7402 0.0292969 21.2109 0.0292969L3.79883 0.0292969C1.2793 0.0292969 0 1.28906 0 3.78906L0 19.2188C0 21.7188 1.2793 22.9785 3.79883 22.9785ZM3.66211 21.25C2.41211 21.25 1.72852 20.5859 1.72852 19.2969L1.72852 7.64648C1.72852 6.34766 2.41211 5.69336 3.66211 5.69336L21.3379 5.69336C22.5781 5.69336 23.2812 6.34766 23.2812 7.64648L23.2812 19.2969C23.2812 20.5859 22.5781 21.25 21.3379 21.25Z", fillAlpha = 0.425f)
            addSfPath("M13.1055 19.043C13.6328 19.043 13.9258 18.7012 13.9258 18.1445L13.9258 8.80859C13.9258 8.23242 13.6035 7.90039 13.0566 7.90039C12.6855 7.90039 12.4121 7.99805 11.9824 8.30078L9.75586 9.79492C9.53125 9.95117 9.43359 10.1172 9.43359 10.3711C9.43359 10.7031 9.69727 10.9961 10.0293 10.9961C10.1758 10.9961 10.293 10.9668 10.5078 10.8203L12.2266 9.6875L12.3145 9.6875L12.3145 18.1445C12.3145 18.7012 12.6172 19.043 13.1055 19.043Z", fillAlpha = 0.85f)
        }
        return _sF1Calendar!!
    }

private var _sF1Calendar: ImageVector? = null
