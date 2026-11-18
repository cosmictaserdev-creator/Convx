package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLightPanelFill (monochrome)
 * Viewport: 30.9375 x 23.1152
 */
public val SfSymbols.Monochrome.SFLightPanelFill: ImageVector
    get() {
        if (_sFLightPanelFill != null) {
            return _sFLightPanelFill!!
        }
        _sFLightPanelFill = sfIcon(
            name = "Monochrome.SFLightPanelFill",
            viewportWidth = 30.9375f,
            viewportHeight = 23.1152f
        ) {
            addSfPath("M3.79883 23.1055L26.7676 23.1055C29.2969 23.1055 30.5762 21.8262 30.5762 19.3359L30.5762 3.75977C30.5762 1.26953 29.2969 0 26.7676 0L3.79883 0C1.2793 0 0 1.25977 0 3.75977L0 19.3359C0 21.8359 1.2793 23.1055 3.79883 23.1055ZM3.83789 21.3672C2.4707 21.3672 1.72852 20.6445 1.72852 19.248L1.72852 3.84766C1.72852 2.45117 2.4707 1.72852 3.83789 1.72852L26.7383 1.72852C28.0762 1.72852 28.8477 2.45117 28.8477 3.84766L28.8477 19.248C28.8477 20.6445 28.0762 21.3672 26.7383 21.3672Z", fillAlpha = 0.85f)
            addSfPath("M4.51172 19.8145L26.0547 19.8145C26.8457 19.8145 27.2852 19.3652 27.2852 18.5645L27.2852 4.53125C27.2852 3.74023 26.8457 3.28125 26.0547 3.28125L4.51172 3.28125C3.73047 3.28125 3.29102 3.74023 3.29102 4.53125L3.29102 18.5645C3.29102 19.3652 3.73047 19.8145 4.51172 19.8145Z", fillAlpha = 0.85f)
        }
        return _sFLightPanelFill!!
    }

private var _sFLightPanelFill: ImageVector? = null
