package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMessageCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFMessageCircle: ImageVector
    get() {
        if (_sFMessageCircle != null) {
            return _sFMessageCircle!!
        }
        _sFMessageCircle = sfIcon(
            name = "Dualtone.SFMessageCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M12.6953 18.5352C16.7578 18.5352 19.6973 16.0938 19.6973 12.7246C19.6973 9.3457 16.7578 6.91406 12.6953 6.91406C8.64258 6.91406 5.70312 9.3457 5.70312 12.7246C5.70312 15.166 7.05078 15.8008 7.05078 16.9238C7.05078 17.4023 6.875 17.7246 6.51367 18.0566C6.2793 18.2715 6.39648 18.5352 6.73828 18.5352C7.55859 18.5352 8.4375 18.2324 9.05273 17.7734C10.1074 18.2812 11.3379 18.5352 12.6953 18.5352Z", fillAlpha = 0.85f)
        }
        return _sFMessageCircle!!
    }

private var _sFMessageCircle: ImageVector? = null
