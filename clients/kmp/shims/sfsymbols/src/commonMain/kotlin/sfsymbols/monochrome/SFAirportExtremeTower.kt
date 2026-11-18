package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFAirportExtremeTower (monochrome)
 * Viewport: 16.7188 x 28.291
 */
public val SfSymbols.Monochrome.SFAirportExtremeTower: ImageVector
    get() {
        if (_sFAirportExtremeTower != null) {
            return _sFAirportExtremeTower!!
        }
        _sFAirportExtremeTower = sfIcon(
            name = "Monochrome.SFAirportExtremeTower",
            viewportWidth = 16.7188f,
            viewportHeight = 28.291f
        ) {
            addSfPath("M11.9824 24.9512C12.5586 24.9512 13.0176 24.4824 13.0176 23.8965C13.0176 23.3301 12.5586 22.8613 11.9824 22.8613C11.3965 22.8613 10.9277 23.3301 10.9277 23.8965C10.9277 24.4824 11.3965 24.9512 11.9824 24.9512ZM0 24.5312C0 27.0312 1.2793 28.291 3.79883 28.291L12.5586 28.291C15.0781 28.291 16.3574 27.0312 16.3574 24.5312L16.3574 5.94727C16.3574 5.0293 16.1523 4.44336 15.6055 3.7793L13.6816 1.42578C12.9199 0.498047 12.1289 0.00976562 11.123 0.00976562L5.23438 0.00976562C4.22852 0.00976562 3.4375 0.498047 2.67578 1.42578L0.751953 3.7793C0.205078 4.44336 0 5.0293 0 5.94727ZM1.72852 24.4336L1.72852 6.8457C2.04102 7.06055 2.41211 7.14844 2.90039 7.14844L13.457 7.14844C13.9453 7.14844 14.3164 7.06055 14.6289 6.8457L14.6289 24.4336C14.6289 25.8398 13.8867 26.5625 12.5195 26.5625L3.83789 26.5625C2.4707 26.5625 1.72852 25.8398 1.72852 24.4336ZM2.32422 4.38477L3.94531 2.40234C4.49219 1.74805 4.80469 1.39648 5.6543 1.39648L10.7031 1.39648C11.5527 1.39648 11.8652 1.74805 12.4121 2.40234L14.0332 4.38477C14.3359 4.75586 14.2773 5.41992 13.4961 5.41992L2.86133 5.41992C2.08008 5.41992 2.02148 4.75586 2.32422 4.38477Z", fillAlpha = 0.85f)
        }
        return _sFAirportExtremeTower!!
    }

private var _sFAirportExtremeTower: ImageVector? = null
