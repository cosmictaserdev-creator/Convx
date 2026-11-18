package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTriangleCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFTriangleCircle: ImageVector
    get() {
        if (_sFTriangleCircle != null) {
            return _sFTriangleCircle!!
        }
        _sFTriangleCircle = sfIcon(
            name = "Monochrome.SFTriangleCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M6.46484 15.2344C5.69336 16.5723 6.26953 17.8711 7.8125 17.8711L17.627 17.8711C19.1699 17.8711 19.7461 16.5723 18.9746 15.2344L14.2188 6.97266C13.5059 5.72266 11.9727 5.6543 11.1914 7.01172ZM7.88086 15.8105L12.4023 7.91992C12.5488 7.66602 12.8809 7.66602 13.0176 7.91992L17.5586 15.8105C17.7246 16.084 17.6758 16.3574 17.2949 16.3574L8.14453 16.3574C7.76367 16.3574 7.71484 16.084 7.88086 15.8105Z", fillAlpha = 0.85f)
        }
        return _sFTriangleCircle!!
    }

private var _sFTriangleCircle: ImageVector? = null
