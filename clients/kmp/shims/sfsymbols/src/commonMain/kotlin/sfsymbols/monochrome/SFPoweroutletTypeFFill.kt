package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPoweroutletTypeFFill (monochrome)
 * Viewport: 28.2031 x 26.2012
 */
public val SfSymbols.Monochrome.SFPoweroutletTypeFFill: ImageVector
    get() {
        if (_sFPoweroutletTypeFFill != null) {
            return _sFPoweroutletTypeFFill!!
        }
        _sFPoweroutletTypeFFill = sfIcon(
            name = "Monochrome.SFPoweroutletTypeFFill",
            viewportWidth = 28.2031f,
            viewportHeight = 26.2012f
        ) {
            addSfPath("M25.2637 8.11523L25.2637 9.36523L26.3184 9.36523C27.2363 9.36523 27.8418 9.98047 27.8418 10.8887L27.8418 15.293C27.8418 16.2109 27.2363 16.8164 26.3184 16.8164L25.2637 16.8164L25.2637 18.0762C25.2637 22.3242 19.8145 26.1816 13.9258 26.1816C8.02734 26.1816 2.57812 22.3242 2.57812 18.0762L2.57812 16.8164L1.52344 16.8164C0.605469 16.8164 0 16.2109 0 15.293L0 10.8887C0 9.98047 0.605469 9.36523 1.52344 9.36523L2.57812 9.36523L2.57812 8.11523C2.57812 3.85742 8.02734 0 13.9258 0C19.8145 0 25.2637 3.85742 25.2637 8.11523ZM5.55664 13.0957C5.55664 14.4238 6.64062 15.498 7.96875 15.498C9.30664 15.498 10.3809 14.4238 10.3809 13.0957C10.3809 11.7676 9.30664 10.6836 7.96875 10.6836C6.64062 10.6836 5.55664 11.7676 5.55664 13.0957ZM17.4609 13.0957C17.4609 14.4238 18.5352 15.498 19.873 15.498C21.2012 15.498 22.2852 14.4238 22.2852 13.0957C22.2852 11.7676 21.2012 10.6836 19.873 10.6836C18.5352 10.6836 17.4609 11.7676 17.4609 13.0957Z", fillAlpha = 0.85f)
        }
        return _sFPoweroutletTypeFFill!!
    }

private var _sFPoweroutletTypeFFill: ImageVector? = null
