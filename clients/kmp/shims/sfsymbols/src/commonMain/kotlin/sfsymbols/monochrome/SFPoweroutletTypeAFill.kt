package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPoweroutletTypeAFill (monochrome)
 * Viewport: 27.1973 x 21.4941
 */
public val SfSymbols.Monochrome.SFPoweroutletTypeAFill: ImageVector
    get() {
        if (_sFPoweroutletTypeAFill != null) {
            return _sFPoweroutletTypeAFill!!
        }
        _sFPoweroutletTypeAFill = sfIcon(
            name = "Monochrome.SFPoweroutletTypeAFill",
            viewportWidth = 27.1973f,
            viewportHeight = 21.4941f
        ) {
            addSfPath("M22.5977 0.908203C25.2637 3.44727 26.8359 7.12891 26.8359 10.7422C26.8359 14.3555 25.2637 18.0273 22.5977 20.5664C21.9824 21.1621 21.1621 21.4648 20.2734 21.4648L6.55273 21.4648C5.67383 21.4648 4.85352 21.1621 4.22852 20.5664C1.5625 18.0273 0 14.3555 0 10.7422C0 7.16797 1.54297 3.51562 4.13086 0.986328C4.77539 0.351562 5.64453 0 6.55273 0L20.2734 0C21.1621 0 21.9824 0.322266 22.5977 0.908203ZM7.01172 6.75781L7.01172 14.8438C7.01172 15.4102 7.43164 15.8301 7.99805 15.8301C8.56445 15.8301 9.02344 15.4102 9.02344 14.8438L9.02344 6.75781C9.02344 6.19141 8.56445 5.73242 7.99805 5.73242C7.43164 5.73242 7.01172 6.19141 7.01172 6.75781ZM17.8125 6.76758L17.8125 14.8145C17.8125 15.3809 18.252 15.8301 18.8184 15.8301C19.3945 15.8301 19.8242 15.3906 19.8242 14.8145L19.8242 6.76758C19.8242 6.19141 19.3945 5.73242 18.8184 5.73242C18.252 5.73242 17.8125 6.19141 17.8125 6.76758Z", fillAlpha = 0.85f)
        }
        return _sFPoweroutletTypeAFill!!
    }

private var _sFPoweroutletTypeAFill: ImageVector? = null
