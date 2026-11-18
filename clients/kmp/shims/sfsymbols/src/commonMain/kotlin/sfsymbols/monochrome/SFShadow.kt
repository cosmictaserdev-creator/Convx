package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFShadow (monochrome)
 * Viewport: 17.3926 x 20.4734
 */
public val SfSymbols.Monochrome.SFShadow: ImageVector
    get() {
        if (_sFShadow != null) {
            return _sFShadow!!
        }
        _sFShadow = sfIcon(
            name = "Monochrome.SFShadow",
            viewportWidth = 17.3926f,
            viewportHeight = 20.4734f
        ) {
            addSfPath("M0 19.5873L2.96875 20.3783C4.17969 20.7006 4.91211 20.1927 4.91211 19.0209L4.91211 14.3236L11.1523 14.3236L11.1523 10.8861L1.00586 10.8861L1.00586 18.5912C1.00586 19.1966 0.615234 19.5873 0 19.5873ZM1.00586 9.11852L4.91211 9.11852L4.91211 3.63024C4.91211 2.57556 4.43359 2.05798 3.4668 1.80407L0.585938 1.03259C0.839844 1.1986 1.00586 1.47204 1.00586 1.87243ZM12.1094 19.5873L15.0781 20.3783C16.2891 20.7006 17.0312 20.1927 17.0312 19.0209L17.0312 3.63024C17.0312 2.57556 16.5332 2.04821 15.5664 1.79431L12.6953 1.03259C12.9492 1.1986 13.1152 1.47204 13.1152 1.87243L13.1152 18.5912C13.1152 19.1966 12.7246 19.5873 12.1094 19.5873Z", fillAlpha = 0.85f)
        }
        return _sFShadow!!
    }

private var _sFShadow: ImageVector? = null
