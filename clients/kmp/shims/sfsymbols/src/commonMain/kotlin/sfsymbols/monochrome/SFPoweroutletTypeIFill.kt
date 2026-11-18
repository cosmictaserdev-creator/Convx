package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPoweroutletTypeIFill (monochrome)
 * Viewport: 24.1406 x 22.9785
 */
public val SfSymbols.Monochrome.SFPoweroutletTypeIFill: ImageVector
    get() {
        if (_sFPoweroutletTypeIFill != null) {
            return _sFPoweroutletTypeIFill!!
        }
        _sFPoweroutletTypeIFill = sfIcon(
            name = "Monochrome.SFPoweroutletTypeIFill",
            viewportWidth = 24.1406f,
            viewportHeight = 22.9785f
        ) {
            addSfPath("M23.7793 3.78906L23.7793 19.2188C23.7793 21.709 22.5098 22.9785 19.9805 22.9785L3.79883 22.9785C1.2793 22.9785 0 21.7188 0 19.2188L0 3.78906C0 1.28906 1.2793 0.0292969 3.79883 0.0292969L19.9805 0.0292969C22.5098 0.0292969 23.7793 1.29883 23.7793 3.78906ZM11.3965 12.5195C10.9863 12.5195 10.7031 12.8125 10.7031 13.2422L10.7031 17.1484C10.7031 17.5684 10.9766 17.8418 11.3965 17.8418L12.373 17.8418C12.793 17.8418 13.0859 17.5781 13.0859 17.1484L13.0859 13.2422C13.0859 12.8125 12.793 12.5195 12.373 12.5195ZM8.34961 6.18164L5.3125 9.19922C5.00977 9.50195 5.00977 9.91211 5.30273 10.2051L5.99609 10.8984C6.28906 11.2012 6.68945 11.2012 7.00195 10.8984L10.0391 7.87109C10.332 7.56836 10.332 7.16797 10.0293 6.85547L9.33594 6.17188C9.04297 5.86914 8.66211 5.86914 8.34961 6.18164ZM14.4434 6.16211L13.75 6.85547C13.457 7.16797 13.457 7.55859 13.75 7.86133L16.7871 10.8887C17.0898 11.2012 17.4902 11.2012 17.793 10.8887L18.4863 10.1953C18.7793 9.91211 18.7793 9.51172 18.4668 9.19922L15.4492 6.17188C15.1465 5.86914 14.7461 5.86914 14.4434 6.16211Z", fillAlpha = 0.85f)
        }
        return _sFPoweroutletTypeIFill!!
    }

private var _sFPoweroutletTypeIFill: ImageVector? = null
