package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPoweroutletTypeBFill (monochrome)
 * Viewport: 24.1406 x 22.9785
 */
public val SfSymbols.Monochrome.SFPoweroutletTypeBFill: ImageVector
    get() {
        if (_sFPoweroutletTypeBFill != null) {
            return _sFPoweroutletTypeBFill!!
        }
        _sFPoweroutletTypeBFill = sfIcon(
            name = "Monochrome.SFPoweroutletTypeBFill",
            viewportWidth = 24.1406f,
            viewportHeight = 22.9785f
        ) {
            addSfPath("M23.7793 3.78906L23.7793 19.2188C23.7793 21.709 22.5098 22.9785 19.9805 22.9785L3.79883 22.9785C1.2793 22.9785 0 21.7188 0 19.2188L0 3.78906C0 1.28906 1.2793 0.0292969 3.79883 0.0292969L19.9805 0.0292969C22.5098 0.0292969 23.7793 1.29883 23.7793 3.78906ZM9.78516 16.6309L9.78516 18.1445C9.78516 18.4961 9.96094 18.6816 10.3223 18.6816L13.4668 18.6816C13.8281 18.6816 14.0137 18.4961 14.0137 18.1445L14.0137 16.6309C14.0137 15.4492 13.0762 14.4922 11.8945 14.4922C10.7031 14.4922 9.78516 15.4492 9.78516 16.6309ZM6.42578 6.20117L6.42578 10.3125C6.42578 10.8984 6.85547 11.3281 7.42188 11.3281C7.98828 11.3281 8.44727 10.8887 8.44727 10.3125L8.44727 6.20117C8.44727 5.63477 7.98828 5.16602 7.42188 5.16602C6.85547 5.16602 6.42578 5.63477 6.42578 6.20117ZM15.332 6.20117L15.332 10.3125C15.332 10.8984 15.7617 11.3281 16.3281 11.3281C16.9043 11.3281 17.3535 10.8887 17.3535 10.3125L17.3535 6.20117C17.3535 5.63477 16.9043 5.16602 16.3281 5.16602C15.7617 5.16602 15.332 5.63477 15.332 6.20117Z", fillAlpha = 0.85f)
        }
        return _sFPoweroutletTypeBFill!!
    }

private var _sFPoweroutletTypeBFill: ImageVector? = null
