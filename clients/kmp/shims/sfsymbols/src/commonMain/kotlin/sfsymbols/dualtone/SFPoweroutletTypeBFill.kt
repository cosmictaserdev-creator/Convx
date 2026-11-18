package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPoweroutletTypeBFill (dualtone)
 * Viewport: 24.1406 x 22.9785
 */
public val SfSymbols.Dualtone.SFPoweroutletTypeBFill: ImageVector
    get() {
        if (_sFPoweroutletTypeBFill != null) {
            return _sFPoweroutletTypeBFill!!
        }
        _sFPoweroutletTypeBFill = sfIcon(
            name = "Dualtone.SFPoweroutletTypeBFill",
            viewportWidth = 24.1406f,
            viewportHeight = 22.9785f
        ) {
            addSfPath("M3.79883 22.9785L19.9805 22.9785C22.5098 22.9785 23.7793 21.709 23.7793 19.2188L23.7793 3.78906C23.7793 1.29883 22.5098 0.0292969 19.9805 0.0292969L3.79883 0.0292969C1.2793 0.0292969 0 1.28906 0 3.78906L0 19.2188C0 21.7188 1.2793 22.9785 3.79883 22.9785Z", fillAlpha = 0.2125f)
            addSfPath("M7.42188 11.3281C6.85547 11.3281 6.42578 10.8984 6.42578 10.3125L6.42578 6.20117C6.42578 5.63477 6.85547 5.16602 7.42188 5.16602C7.98828 5.16602 8.44727 5.63477 8.44727 6.20117L8.44727 10.3125C8.44727 10.8887 7.98828 11.3281 7.42188 11.3281ZM10.3223 18.6816C9.96094 18.6816 9.78516 18.4961 9.78516 18.1445L9.78516 16.6309C9.78516 15.4492 10.7031 14.4922 11.8945 14.4922C13.0762 14.4922 14.0137 15.4492 14.0137 16.6309L14.0137 18.1445C14.0137 18.4961 13.8281 18.6816 13.4668 18.6816ZM16.3281 11.3281C15.7617 11.3281 15.332 10.8984 15.332 10.3125L15.332 6.20117C15.332 5.63477 15.7617 5.16602 16.3281 5.16602C16.9043 5.16602 17.3535 5.63477 17.3535 6.20117L17.3535 10.3125C17.3535 10.8887 16.9043 11.3281 16.3281 11.3281Z", fillAlpha = 0.85f)
        }
        return _sFPoweroutletTypeBFill!!
    }

private var _sFPoweroutletTypeBFill: ImageVector? = null
