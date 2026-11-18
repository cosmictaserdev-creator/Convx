package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLightswitchOnFill (monochrome)
 * Viewport: 18.3594 x 27.5879
 */
public val SfSymbols.Monochrome.SFLightswitchOnFill: ImageVector
    get() {
        if (_sFLightswitchOnFill != null) {
            return _sFLightswitchOnFill!!
        }
        _sFLightswitchOnFill = sfIcon(
            name = "Monochrome.SFLightswitchOnFill",
            viewportWidth = 18.3594f,
            viewportHeight = 27.5879f
        ) {
            addSfPath("M17.998 3.80859L17.998 23.7891C17.998 26.3086 16.7285 27.5879 14.2383 27.5879L3.75977 27.5879C1.25977 27.5879 0 26.3086 0 23.7891L0 3.80859C0 1.2793 1.25977 0.00976562 3.75977 0.00976562L14.2383 0.00976562C16.7285 0.00976562 17.998 1.2793 17.998 3.80859ZM4.20898 2.99805C3.30078 2.99805 2.91016 3.37891 2.91016 4.27734L2.91016 10.2637C2.91016 11.1621 3.30078 11.543 4.20898 11.543L13.75 11.543C14.6484 11.543 15.0488 11.1621 15.0488 10.2637L15.0488 4.27734C15.0488 3.37891 14.6484 2.99805 13.75 2.99805Z", fillAlpha = 0.85f)
        }
        return _sFLightswitchOnFill!!
    }

private var _sFLightswitchOnFill: ImageVector? = null
