package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPauseCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFPauseCircle: ImageVector
    get() {
        if (_sFPauseCircle != null) {
            return _sFPauseCircle!!
        }
        _sFPauseCircle = sfIcon(
            name = "Monochrome.SFPauseCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M9.18945 17.5879L10.4395 17.5879C11.0352 17.5879 11.3086 17.2656 11.3086 16.7871L11.3086 8.63281C11.3086 8.16406 11.0352 7.8418 10.4395 7.8418L9.18945 7.8418C8.60352 7.8418 8.32031 8.16406 8.32031 8.63281L8.32031 16.7871C8.32031 17.2656 8.60352 17.5879 9.18945 17.5879ZM14.9902 17.5879L16.2402 17.5879C16.8164 17.5879 17.0996 17.2656 17.0996 16.7871L17.0996 8.63281C17.0996 8.16406 16.8164 7.8418 16.2402 7.8418L14.9902 7.8418C14.3945 7.8418 14.1113 8.16406 14.1113 8.63281L14.1113 16.7871C14.1113 17.2656 14.3945 17.5879 14.9902 17.5879Z", fillAlpha = 0.85f)
        }
        return _sFPauseCircle!!
    }

private var _sFPauseCircle: ImageVector? = null
