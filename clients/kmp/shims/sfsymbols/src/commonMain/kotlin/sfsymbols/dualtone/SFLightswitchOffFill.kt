package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLightswitchOffFill (dualtone)
 * Viewport: 18.3594 x 27.5879
 */
public val SfSymbols.Dualtone.SFLightswitchOffFill: ImageVector
    get() {
        if (_sFLightswitchOffFill != null) {
            return _sFLightswitchOffFill!!
        }
        _sFLightswitchOffFill = sfIcon(
            name = "Dualtone.SFLightswitchOffFill",
            viewportWidth = 18.3594f,
            viewportHeight = 27.5879f
        ) {
            addSfPath("M3.75977 0.00976562C1.25977 0.00976562 0 1.28906 0 3.80859L0 23.7891C0 26.3184 1.25977 27.5879 3.75977 27.5879L14.2383 27.5879C16.7285 27.5879 17.998 26.3184 17.998 23.7891L17.998 3.80859C17.998 1.28906 16.7285 0.00976562 14.2383 0.00976562Z", fillAlpha = 0.2125f)
            addSfPath("M4.20898 16.0547L13.75 16.0547C14.6484 16.0547 15.0488 16.4355 15.0488 17.334L15.0488 23.3203C15.0488 24.2188 14.6484 24.5996 13.75 24.5996L4.20898 24.5996C3.30078 24.5996 2.91016 24.2188 2.91016 23.3203L2.91016 17.334C2.91016 16.4355 3.30078 16.0547 4.20898 16.0547Z", fillAlpha = 0.85f)
        }
        return _sFLightswitchOffFill!!
    }

private var _sFLightswitchOffFill: ImageVector? = null
