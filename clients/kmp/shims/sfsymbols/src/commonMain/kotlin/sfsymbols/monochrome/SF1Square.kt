package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SF1Square (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SF1Square: ImageVector
    get() {
        if (_sF1Square != null) {
            return _sF1Square!!
        }
        _sF1Square = sfIcon(
            name = "Monochrome.SF1Square",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.85f)
            addSfPath("M12.0996 17.4121C12.6562 17.4121 12.9688 17.0508 12.9688 16.4453L12.9688 6.50391C12.9688 5.88867 12.627 5.53711 12.0508 5.53711C11.6504 5.53711 11.3672 5.64453 10.8984 5.9668L8.53516 7.54883C8.29102 7.71484 8.19336 7.89062 8.19336 8.17383C8.19336 8.51562 8.4668 8.82812 8.81836 8.82812C8.98438 8.82812 9.10156 8.79883 9.33594 8.64258L11.1621 7.44141L11.25 7.44141L11.25 16.4453C11.25 17.0508 11.5723 17.4121 12.0996 17.4121Z", fillAlpha = 0.85f)
        }
        return _sF1Square!!
    }

private var _sF1Square: ImageVector? = null
