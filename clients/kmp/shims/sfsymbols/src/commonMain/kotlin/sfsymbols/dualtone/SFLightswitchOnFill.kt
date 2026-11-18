package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLightswitchOnFill (dualtone)
 * Viewport: 18.3594 x 27.5879
 */
public val SfSymbols.Dualtone.SFLightswitchOnFill: ImageVector
    get() {
        if (_sFLightswitchOnFill != null) {
            return _sFLightswitchOnFill!!
        }
        _sFLightswitchOnFill = sfIcon(
            name = "Dualtone.SFLightswitchOnFill",
            viewportWidth = 18.3594f,
            viewportHeight = 27.5879f
        ) {
            addSfPath("M3.75977 27.5879L14.2383 27.5879C16.7285 27.5879 17.998 26.3086 17.998 23.7891L17.998 3.80859C17.998 1.2793 16.7285 0.00976562 14.2383 0.00976562L3.75977 0.00976562C1.25977 0.00976562 0 1.2793 0 3.80859L0 23.7891C0 26.3086 1.25977 27.5879 3.75977 27.5879Z", fillAlpha = 0.2125f)
            addSfPath("M4.20898 11.543C3.30078 11.543 2.91016 11.1621 2.91016 10.2637L2.91016 4.27734C2.91016 3.37891 3.30078 2.99805 4.20898 2.99805L13.75 2.99805C14.6484 2.99805 15.0488 3.37891 15.0488 4.27734L15.0488 10.2637C15.0488 11.1621 14.6484 11.543 13.75 11.543Z", fillAlpha = 0.85f)
        }
        return _sFLightswitchOnFill!!
    }

private var _sFLightswitchOnFill: ImageVector? = null
