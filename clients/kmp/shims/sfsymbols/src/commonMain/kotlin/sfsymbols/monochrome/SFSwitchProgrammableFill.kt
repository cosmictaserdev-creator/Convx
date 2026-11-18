package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSwitchProgrammableFill (monochrome)
 * Viewport: 18.3594 x 27.5879
 */
public val SfSymbols.Monochrome.SFSwitchProgrammableFill: ImageVector
    get() {
        if (_sFSwitchProgrammableFill != null) {
            return _sFSwitchProgrammableFill!!
        }
        _sFSwitchProgrammableFill = sfIcon(
            name = "Monochrome.SFSwitchProgrammableFill",
            viewportWidth = 18.3594f,
            viewportHeight = 27.5879f
        ) {
            addSfPath("M17.998 3.80859L17.998 23.7891C17.998 26.3184 16.7285 27.5879 14.2383 27.5879L3.75977 27.5879C1.25977 27.5879 0 26.3184 0 23.7891L0 3.80859C0 1.28906 1.25977 0.00976562 3.75977 0.00976562L14.2383 0.00976562C16.7285 0.00976562 17.998 1.28906 17.998 3.80859ZM4.41406 16.2988C3.53516 16.2988 3.14453 16.6797 3.14453 17.5488L3.14453 23.291C3.14453 24.1699 3.53516 24.541 4.41406 24.541L13.5352 24.541C14.4238 24.541 14.8145 24.1699 14.8145 23.291L14.8145 17.5488C14.8145 16.6797 14.4238 16.2988 13.5352 16.2988ZM4.41406 3.05664C3.53516 3.05664 3.14453 3.42773 3.14453 4.30664L3.14453 10.0488C3.14453 10.918 3.53516 11.2988 4.41406 11.2988L13.5352 11.2988C14.4238 11.2988 14.8145 10.918 14.8145 10.0488L14.8145 4.30664C14.8145 3.42773 14.4238 3.05664 13.5352 3.05664Z", fillAlpha = 0.85f)
        }
        return _sFSwitchProgrammableFill!!
    }

private var _sFSwitchProgrammableFill: ImageVector? = null
