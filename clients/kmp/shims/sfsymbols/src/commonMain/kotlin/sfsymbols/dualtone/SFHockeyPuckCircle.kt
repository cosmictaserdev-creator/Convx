package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFHockeyPuckCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFHockeyPuckCircle: ImageVector
    get() {
        if (_sFHockeyPuckCircle != null) {
            return _sFHockeyPuckCircle!!
        }
        _sFHockeyPuckCircle = sfIcon(
            name = "Dualtone.SFHockeyPuckCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M12.7148 13.6914C16.5332 13.6914 19.5215 12.334 19.5215 10.6152C19.5215 8.93555 16.5332 7.60742 12.7148 7.60742C8.89648 7.60742 5.9082 8.93555 5.9082 10.6152C5.9082 12.334 8.89648 13.6914 12.7148 13.6914ZM12.7148 17.832C16.582 17.832 19.5215 16.123 19.5215 14.0332L19.5215 12.4805C18.2617 13.7793 15.5957 14.5312 12.7148 14.5312C9.84375 14.5312 7.17773 13.7793 5.9082 12.4805L5.9082 14.0332C5.9082 16.123 8.83789 17.832 12.7148 17.832Z", fillAlpha = 0.85f)
        }
        return _sFHockeyPuckCircle!!
    }

private var _sFHockeyPuckCircle: ImageVector? = null
