package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFStarCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFStarCircle: ImageVector
    get() {
        if (_sFStarCircle != null) {
            return _sFStarCircle!!
        }
        _sFStarCircle = sfIcon(
            name = "Dualtone.SFStarCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M9.01367 19.1992L12.7051 16.5039L16.3965 19.1992C17.041 19.6875 17.7344 19.1895 17.4805 18.418L16.0352 14.0625L19.7559 11.4062C20.332 10.9863 20.1562 10.1074 19.3359 10.1172L14.7656 10.1465L13.3691 5.77148C13.1348 5.03906 12.2754 5.03906 12.0508 5.77148L10.6543 10.1465L6.07422 10.1172C5.26367 10.1074 5.05859 10.9668 5.66406 11.416L9.38477 14.0625L7.93945 18.418C7.68555 19.1895 8.37891 19.6875 9.01367 19.1992Z", fillAlpha = 0.85f)
        }
        return _sFStarCircle!!
    }

private var _sFStarCircle: ImageVector? = null
