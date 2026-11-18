package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTentCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFTentCircle: ImageVector
    get() {
        if (_sFTentCircle != null) {
            return _sFTentCircle!!
        }
        _sFTentCircle = sfIcon(
            name = "Monochrome.SFTentCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M6.46484 18.6816L18.9648 18.6816C19.7461 18.6816 19.9707 17.9883 19.668 17.373L13.6719 5.3418C13.2324 4.45312 12.207 4.47266 11.7871 5.32227L5.77148 17.373C5.46875 17.9883 5.69336 18.6816 6.46484 18.6816ZM9.3457 17.6953L12.3242 11.3281C12.4121 11.1426 12.5684 11.0547 12.7148 11.0547C12.8809 11.0547 13.0273 11.1426 13.1152 11.3281L16.084 17.6953L13.8086 17.6953L12.7148 12.793L11.6211 17.6953Z", fillAlpha = 0.85f)
        }
        return _sFTentCircle!!
    }

private var _sFTentCircle: ImageVector? = null
