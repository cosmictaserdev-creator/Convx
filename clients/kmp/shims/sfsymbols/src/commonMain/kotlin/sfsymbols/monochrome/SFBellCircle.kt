package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBellCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFBellCircle: ImageVector
    get() {
        if (_sFBellCircle != null) {
            return _sFBellCircle!!
        }
        _sFBellCircle = sfIcon(
            name = "Monochrome.SFBellCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M7.05078 17.2754L18.3789 17.2754C18.9258 17.2754 19.2578 16.9824 19.2578 16.543C19.2578 15.8398 18.5742 15.2344 17.9688 14.6094C17.4219 14.0234 17.373 12.8516 17.2852 11.8555C17.1875 9.38477 16.4648 7.64648 14.668 7.03125C14.4141 6.10352 13.7012 5.40039 12.7148 5.40039C11.7285 5.40039 11.0156 6.10352 10.7715 7.03125C8.96484 7.64648 8.25195 9.38477 8.1543 11.8555C8.05664 12.8516 7.98828 14.0234 7.46094 14.6094C6.875 15.2441 6.18164 15.8398 6.18164 16.543C6.18164 16.9824 6.50391 17.2754 7.05078 17.2754ZM12.7148 20.1074C13.8672 20.1074 14.6973 19.3066 14.7949 18.2812L10.6348 18.2812C10.7422 19.3066 11.5625 20.1074 12.7148 20.1074Z", fillAlpha = 0.85f)
        }
        return _sFBellCircle!!
    }

private var _sFBellCircle: ImageVector? = null
