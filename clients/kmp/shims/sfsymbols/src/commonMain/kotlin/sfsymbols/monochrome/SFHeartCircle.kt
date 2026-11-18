package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFHeartCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFHeartCircle: ImageVector
    get() {
        if (_sFHeartCircle != null) {
            return _sFHeartCircle!!
        }
        _sFHeartCircle = sfIcon(
            name = "Monochrome.SFHeartCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M9.69727 7.13867C7.56836 7.13867 6.03516 8.75 6.03516 10.9473C6.03516 14.3457 9.6582 17.4219 12.1387 18.9844C12.3438 19.1113 12.5879 19.2578 12.7344 19.2578C12.8809 19.2578 13.1152 19.1113 13.291 18.9844C15.7324 17.3535 19.4043 14.3457 19.4043 10.9473C19.4043 8.75 17.8613 7.13867 15.7422 7.13867C14.4141 7.13867 13.3301 7.92969 12.7148 9.05273C12.0996 7.92969 11.0352 7.13867 9.69727 7.13867Z", fillAlpha = 0.85f)
        }
        return _sFHeartCircle!!
    }

private var _sFHeartCircle: ImageVector? = null
