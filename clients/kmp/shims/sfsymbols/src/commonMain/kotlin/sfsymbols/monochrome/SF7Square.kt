package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SF7Square (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SF7Square: ImageVector
    get() {
        if (_sF7Square != null) {
            return _sF7Square!!
        }
        _sF7Square = sfIcon(
            name = "Monochrome.SF7Square",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.85f)
            addSfPath("M9.79492 17.373C10.166 17.373 10.4199 17.2168 10.6445 16.7871L15.4102 7.71484C15.625 7.31445 15.6738 7.08984 15.6738 6.8457C15.6738 6.24023 15.1953 5.79102 14.4727 5.79102L8.30078 5.79102C7.91016 5.79102 7.64648 6.07422 7.64648 6.47461C7.64648 6.86523 7.91992 7.14844 8.30078 7.14844L13.8086 7.14844L13.8086 7.24609L9.08203 16.0352C8.97461 16.2305 8.92578 16.3867 8.92578 16.6016C8.92578 17.0605 9.30664 17.373 9.79492 17.373Z", fillAlpha = 0.85f)
        }
        return _sF7Square!!
    }

private var _sF7Square: ImageVector? = null
