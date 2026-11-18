package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFAirplaneLanded (dualtone)
 * Viewport: 32.1666 x 15.9082
 */
public val SfSymbols.Dualtone.SFAirplaneLanded: ImageVector
    get() {
        if (_sFAirplaneLanded != null) {
            return _sFAirplaneLanded!!
        }
        _sFAirplaneLanded = sfIcon(
            name = "Dualtone.SFAirplaneLanded",
            viewportWidth = 32.1666f,
            viewportHeight = 15.9082f
        ) {
            addSfPath("M3.49539 15.9082L30.5755 15.9082C31.054 15.9082 31.4446 15.5176 31.4446 15.0391C31.4446 14.5703 31.054 14.1797 30.5755 14.1797L3.49539 14.1797C3.02664 14.1797 2.63601 14.5703 2.63601 15.0391C2.63601 15.5176 3.02664 15.9082 3.49539 15.9082Z", fillAlpha = 0.425f)
            addSfPath("M5.38016 8.69141L27.1282 8.69141C29.7845 8.69141 31.9622 7.39258 31.7962 5.52734C31.6302 3.80859 29.4427 2.72461 26.9817 2.91016L7.25516 4.4043L1.60086 0.527344C1.30789 0.322266 1.07351 0.302734 0.663359 0.517578L0.253202 0.722656C-0.000703999 0.849609-0.0592977 1.07422 0.0578898 1.31836L3.25125 7.65625C3.60281 8.36914 4.32547 8.69141 5.38016 8.69141ZM13.6126 6.29883L13.6126 9.02344C13.6126 10.166 14.2864 10.8398 15.429 10.8398L23.0657 10.8398C24.1985 10.8398 24.8919 10.166 24.8919 9.02344L24.8919 6.29883Z", fillAlpha = 0.85f)
        }
        return _sFAirplaneLanded!!
    }

private var _sFAirplaneLanded: ImageVector? = null
