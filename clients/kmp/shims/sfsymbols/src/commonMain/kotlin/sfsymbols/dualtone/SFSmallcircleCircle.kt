package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSmallcircleCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFSmallcircleCircle: ImageVector
    get() {
        if (_sFSmallcircleCircle != null) {
            return _sFSmallcircleCircle!!
        }
        _sFSmallcircleCircle = sfIcon(
            name = "Dualtone.SFSmallcircleCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M12.7051 17.3438C15.2539 17.3438 17.334 15.2637 17.334 12.7246C17.334 10.1758 15.2539 8.0957 12.7051 8.0957C10.1562 8.0957 8.08594 10.1758 8.08594 12.7246C8.08594 15.2637 10.1562 17.3438 12.7051 17.3438ZM12.7051 15.6445C11.0938 15.6445 9.77539 14.3359 9.77539 12.7246C9.77539 11.1035 11.0938 9.79492 12.7051 9.79492C14.3262 9.79492 15.6348 11.1035 15.6348 12.7246C15.6348 14.3359 14.3262 15.6445 12.7051 15.6445Z", fillAlpha = 0.85f)
        }
        return _sFSmallcircleCircle!!
    }

private var _sFSmallcircleCircle: ImageVector? = null
