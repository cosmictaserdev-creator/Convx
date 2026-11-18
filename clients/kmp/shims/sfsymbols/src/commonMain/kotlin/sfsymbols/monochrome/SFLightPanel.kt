package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLightPanel (monochrome)
 * Viewport: 30.9375 x 23.1152
 */
public val SfSymbols.Monochrome.SFLightPanel: ImageVector
    get() {
        if (_sFLightPanel != null) {
            return _sFLightPanel!!
        }
        _sFLightPanel = sfIcon(
            name = "Monochrome.SFLightPanel",
            viewportWidth = 30.9375f,
            viewportHeight = 23.1152f
        ) {
            addSfPath("M3.79883 23.1055L26.7676 23.1055C29.2969 23.1055 30.5762 21.8262 30.5762 19.3359L30.5762 3.75977C30.5762 1.26953 29.2969 0 26.7676 0L3.79883 0C1.2793 0 0 1.25977 0 3.75977L0 19.3359C0 21.8359 1.2793 23.1055 3.79883 23.1055ZM3.83789 21.3672C2.4707 21.3672 1.72852 20.6445 1.72852 19.248L1.72852 3.84766C1.72852 2.45117 2.4707 1.72852 3.83789 1.72852L26.7383 1.72852C28.0762 1.72852 28.8477 2.45117 28.8477 3.84766L28.8477 19.248C28.8477 20.6445 28.0762 21.3672 26.7383 21.3672Z", fillAlpha = 0.85f)
            addSfPath("M4.51172 19.8145L26.0547 19.8145C26.8457 19.8145 27.2852 19.3652 27.2852 18.5645L27.2852 4.53125C27.2852 3.74023 26.8457 3.28125 26.0547 3.28125L4.51172 3.28125C3.73047 3.28125 3.29102 3.74023 3.29102 4.53125L3.29102 18.5645C3.29102 19.3652 3.73047 19.8145 4.51172 19.8145ZM5.21484 18.3789C4.87305 18.3789 4.72656 18.2324 4.72656 17.8809L4.72656 5.21484C4.72656 4.87305 4.87305 4.7168 5.21484 4.7168L25.3613 4.7168C25.7031 4.7168 25.8496 4.87305 25.8496 5.21484L25.8496 17.8809C25.8496 18.2324 25.7031 18.3789 25.3613 18.3789Z", fillAlpha = 0.85f)
        }
        return _sFLightPanel!!
    }

private var _sFLightPanel: ImageVector? = null
