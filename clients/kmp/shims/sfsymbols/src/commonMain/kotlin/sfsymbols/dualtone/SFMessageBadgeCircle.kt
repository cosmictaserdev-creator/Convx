package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMessageBadgeCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFMessageBadgeCircle: ImageVector
    get() {
        if (_sFMessageBadgeCircle != null) {
            return _sFMessageBadgeCircle!!
        }
        _sFMessageBadgeCircle = sfIcon(
            name = "Dualtone.SFMessageBadgeCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7461 19.7363 25.4395 12.7148 25.4395C5.69336 25.4395 0 19.7461 0 12.7246C0 5.70312 5.69336 0 12.7148 0C19.7363 0 25.4395 5.70312 25.4395 12.7246ZM1.81641 12.7246C1.81641 18.75 6.68945 23.623 12.7148 23.623C18.7402 23.623 23.6133 18.75 23.6133 12.7246C23.6133 6.69922 18.7402 1.82617 12.7148 1.82617C6.68945 1.82617 1.81641 6.69922 1.81641 12.7246Z", fillAlpha = 0.425f)
            addSfPath("M15.137 7.24249C14.7865 7.73251 14.5801 8.3303 14.5801 8.97461C14.5801 10.6445 15.9668 12.0117 17.627 12.0117C18.3347 12.0117 18.9934 11.7555 19.5128 11.3298C19.6362 11.7702 19.6973 12.2376 19.6973 12.7246C19.6973 16.0938 16.7578 18.5352 12.6953 18.5352C11.3379 18.5352 10.1074 18.2812 9.05273 17.7734C8.42773 18.2324 7.55859 18.5352 6.73828 18.5352C6.38672 18.5352 6.2793 18.2715 6.51367 18.0566C6.875 17.7246 7.05078 17.4023 7.05078 16.9238C7.05078 15.8008 5.70312 15.166 5.70312 12.7246C5.70312 9.3457 8.64258 6.91406 12.6953 6.91406C13.5686 6.91406 14.39 7.02642 15.137 7.24249Z", fillAlpha = 0.85f)
            addSfPath("M17.627 11.582C19.043 11.582 20.2148 10.4102 20.2148 8.97461C20.2148 7.53906 19.043 6.37695 17.627 6.37695C16.1914 6.37695 15.0098 7.53906 15.0098 8.97461C15.0098 10.4102 16.1914 11.582 17.627 11.582Z", fillAlpha = 0.85f)
        }
        return _sFMessageBadgeCircle!!
    }

private var _sFMessageBadgeCircle: ImageVector? = null
