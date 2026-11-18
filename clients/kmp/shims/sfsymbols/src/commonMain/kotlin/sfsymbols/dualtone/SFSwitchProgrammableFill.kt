package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSwitchProgrammableFill (dualtone)
 * Viewport: 18.3594 x 27.5879
 */
public val SfSymbols.Dualtone.SFSwitchProgrammableFill: ImageVector
    get() {
        if (_sFSwitchProgrammableFill != null) {
            return _sFSwitchProgrammableFill!!
        }
        _sFSwitchProgrammableFill = sfIcon(
            name = "Dualtone.SFSwitchProgrammableFill",
            viewportWidth = 18.3594f,
            viewportHeight = 27.5879f
        ) {
            addSfPath("M3.75977 27.5879L14.2383 27.5879C16.7285 27.5879 17.998 26.3184 17.998 23.7891L17.998 3.80859C17.998 1.28906 16.7285 0.00976562 14.2383 0.00976562L3.75977 0.00976562C1.25977 0.00976562 0 1.28906 0 3.80859L0 23.7891C0 26.3184 1.25977 27.5879 3.75977 27.5879Z", fillAlpha = 0.2125f)
            addSfPath("M4.41406 11.2988C3.53516 11.2988 3.14453 10.918 3.14453 10.0488L3.14453 4.30664C3.14453 3.42773 3.53516 3.05664 4.41406 3.05664L13.5352 3.05664C14.4238 3.05664 14.8145 3.42773 14.8145 4.30664L14.8145 10.0488C14.8145 10.918 14.4238 11.2988 13.5352 11.2988ZM4.41406 24.541C3.53516 24.541 3.14453 24.1699 3.14453 23.291L3.14453 17.5488C3.14453 16.6797 3.53516 16.2988 4.41406 16.2988L13.5352 16.2988C14.4238 16.2988 14.8145 16.6797 14.8145 17.5488L14.8145 23.291C14.8145 24.1699 14.4238 24.541 13.5352 24.541Z", fillAlpha = 0.85f)
        }
        return _sFSwitchProgrammableFill!!
    }

private var _sFSwitchProgrammableFill: ImageVector? = null
