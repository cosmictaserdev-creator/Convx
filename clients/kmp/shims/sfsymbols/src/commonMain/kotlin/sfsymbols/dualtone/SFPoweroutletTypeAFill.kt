package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPoweroutletTypeAFill (dualtone)
 * Viewport: 27.1973 x 21.4941
 */
public val SfSymbols.Dualtone.SFPoweroutletTypeAFill: ImageVector
    get() {
        if (_sFPoweroutletTypeAFill != null) {
            return _sFPoweroutletTypeAFill!!
        }
        _sFPoweroutletTypeAFill = sfIcon(
            name = "Dualtone.SFPoweroutletTypeAFill",
            viewportWidth = 27.1973f,
            viewportHeight = 21.4941f
        ) {
            addSfPath("M6.55273 21.4648L20.2734 21.4648C21.1621 21.4648 21.9824 21.1621 22.5977 20.5664C25.2637 18.0273 26.8359 14.3555 26.8359 10.7422C26.8359 7.12891 25.2637 3.44727 22.5977 0.908203C21.9824 0.322266 21.1621 0 20.2734 0L6.55273 0C5.64453 0 4.77539 0.351562 4.13086 0.986328C1.54297 3.51562 0 7.16797 0 10.7422C0 14.3555 1.5625 18.0273 4.22852 20.5664C4.85352 21.1621 5.67383 21.4648 6.55273 21.4648Z", fillAlpha = 0.2125f)
            addSfPath("M7.99805 15.8301C7.43164 15.8301 7.01172 15.4102 7.01172 14.8438L7.01172 6.75781C7.01172 6.19141 7.43164 5.73242 7.99805 5.73242C8.56445 5.73242 9.02344 6.19141 9.02344 6.75781L9.02344 14.8438C9.02344 15.4102 8.56445 15.8301 7.99805 15.8301ZM18.8184 15.8301C18.252 15.8301 17.8125 15.3809 17.8125 14.8145L17.8125 6.76758C17.8125 6.19141 18.252 5.73242 18.8184 5.73242C19.3945 5.73242 19.8242 6.19141 19.8242 6.76758L19.8242 14.8145C19.8242 15.3906 19.3945 15.8301 18.8184 15.8301Z", fillAlpha = 0.85f)
        }
        return _sFPoweroutletTypeAFill!!
    }

private var _sFPoweroutletTypeAFill: ImageVector? = null
