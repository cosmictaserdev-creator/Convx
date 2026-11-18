package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFAirplaneDeparture (monochrome)
 * Viewport: 32.3228 x 22.2168
 */
public val SfSymbols.Monochrome.SFAirplaneDeparture: ImageVector
    get() {
        if (_sFAirplaneDeparture != null) {
            return _sFAirplaneDeparture!!
        }
        _sFAirplaneDeparture = sfIcon(
            name = "Monochrome.SFAirplaneDeparture",
            viewportWidth = 32.3228f,
            viewportHeight = 22.2168f
        ) {
            addSfPath("M2.19655 22.2168L29.2766 22.2168C29.7454 22.2168 30.1458 21.8262 30.1458 21.3574C30.1458 20.8789 29.7454 20.4883 29.2766 20.4883L2.19655 20.4883C1.7278 20.4883 1.32741 20.8789 1.32741 21.3574C1.32741 21.8262 1.7278 22.2168 2.19655 22.2168Z", fillAlpha = 0.85f)
            addSfPath("M6.83523 14.0723L28.3001 8.36914C30.761 7.71484 32.47 5.99609 31.8352 4.24805C31.2395 2.65625 28.9153 2.24609 26.718 2.98828L19.3645 5.49805C18.8762 5.66406 18.5051 5.6543 17.9485 5.39062L7.29421 0.361328C6.97194 0.205078 6.63991 0.185547 6.31765 0.273438L5.5071 0.527344C5.21413 0.615234 5.10671 0.957031 5.37038 1.2207L12.5384 8.7793L12.7434 7.76367L6.53249 9.88281L8.64187 9.94141L1.43484 7.40234C1.1028 7.28516 0.829366 7.33398 0.507101 7.62695L0.184835 7.92969C-0.04954 8.14453-0.0593056 8.41797 0.145773 8.64258L4.58913 13.6133C5.11648 14.209 5.87819 14.3262 6.83523 14.0723Z", fillAlpha = 0.85f)
        }
        return _sFAirplaneDeparture!!
    }

private var _sFAirplaneDeparture: ImageVector? = null
