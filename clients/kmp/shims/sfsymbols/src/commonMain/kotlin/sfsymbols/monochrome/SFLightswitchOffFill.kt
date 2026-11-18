package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLightswitchOffFill (monochrome)
 * Viewport: 18.3594 x 27.5879
 */
public val SfSymbols.Monochrome.SFLightswitchOffFill: ImageVector
    get() {
        if (_sFLightswitchOffFill != null) {
            return _sFLightswitchOffFill!!
        }
        _sFLightswitchOffFill = sfIcon(
            name = "Monochrome.SFLightswitchOffFill",
            viewportWidth = 18.3594f,
            viewportHeight = 27.5879f
        ) {
            addSfPath("M17.998 3.80859L17.998 23.7891C17.998 26.3184 16.7285 27.5879 14.2383 27.5879L3.75977 27.5879C1.25977 27.5879 0 26.3184 0 23.7891L0 3.80859C0 1.28906 1.25977 0.00976562 3.75977 0.00976562L14.2383 0.00976562C16.7285 0.00976562 17.998 1.28906 17.998 3.80859ZM4.20898 16.0547C3.30078 16.0547 2.91016 16.4355 2.91016 17.334L2.91016 23.3203C2.91016 24.2188 3.30078 24.5996 4.20898 24.5996L13.75 24.5996C14.6484 24.5996 15.0488 24.2188 15.0488 23.3203L15.0488 17.334C15.0488 16.4355 14.6484 16.0547 13.75 16.0547Z", fillAlpha = 0.85f)
        }
        return _sFLightswitchOffFill!!
    }

private var _sFLightswitchOffFill: ImageVector? = null
