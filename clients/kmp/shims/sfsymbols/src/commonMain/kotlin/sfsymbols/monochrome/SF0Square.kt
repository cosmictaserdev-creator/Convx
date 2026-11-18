package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SF0Square (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SF0Square: ImageVector
    get() {
        if (_sF0Square != null) {
            return _sF0Square!!
        }
        _sF0Square = sfIcon(
            name = "Monochrome.SF0Square",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.85f)
            addSfPath("M11.4844 17.4121C14.0723 17.4121 15.8203 15.0195 15.8203 11.4746C15.8203 7.91992 14.0723 5.53711 11.4844 5.53711C8.89648 5.53711 7.1582 7.91992 7.1582 11.4746C7.1582 15.0195 8.89648 17.4121 11.4844 17.4121ZM11.4844 16.0059C9.89258 16.0059 8.81836 14.1797 8.81836 11.4746C8.81836 8.75977 9.89258 6.94336 11.4844 6.94336C13.0762 6.94336 14.1504 8.75977 14.1504 11.4746C14.1504 14.1797 13.0762 16.0059 11.4844 16.0059Z", fillAlpha = 0.85f)
        }
        return _sF0Square!!
    }

private var _sF0Square: ImageVector? = null
