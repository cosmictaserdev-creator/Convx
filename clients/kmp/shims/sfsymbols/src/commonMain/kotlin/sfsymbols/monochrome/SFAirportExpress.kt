package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFAirportExpress (monochrome)
 * Viewport: 20.5176 x 26.084
 */
public val SfSymbols.Monochrome.SFAirportExpress: ImageVector
    get() {
        if (_sFAirportExpress != null) {
            return _sFAirportExpress!!
        }
        _sFAirportExpress = sfIcon(
            name = "Monochrome.SFAirportExpress",
            viewportWidth = 20.5176f,
            viewportHeight = 26.084f
        ) {
            addSfPath("M3.79883 26.084L16.3574 26.084C18.8672 26.084 20.1562 24.8242 20.1562 22.3242L20.1562 3.80859C20.1562 1.30859 18.8672 0.0488281 16.3574 0.0488281L3.79883 0.0488281C1.2793 0.0488281 0 1.30859 0 3.80859L0 22.3242C0 24.8242 1.2793 26.084 3.79883 26.084ZM3.83789 24.3555C2.4707 24.3555 1.72852 23.6328 1.72852 22.2363L1.72852 3.89648C1.72852 2.5 2.4707 1.77734 3.83789 1.77734L16.3184 1.77734C17.6855 1.77734 18.4277 2.5 18.4277 3.89648L18.4277 22.2363C18.4277 23.6328 17.6855 24.3555 16.3184 24.3555ZM0.195312 8.94531L4.56055 8.94531C6.42578 8.94531 7.36328 8.01758 7.36328 6.18164L7.36328 0.488281L6.02539 0.488281L6.02539 6.09375C6.02539 7.07031 5.48828 7.60742 4.53125 7.60742L0.195312 7.60742Z", fillAlpha = 0.85f)
        }
        return _sFAirportExpress!!
    }

private var _sFAirportExpress: ImageVector? = null
