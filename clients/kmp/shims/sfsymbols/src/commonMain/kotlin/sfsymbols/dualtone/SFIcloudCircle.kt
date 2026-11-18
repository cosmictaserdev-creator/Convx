package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFIcloudCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFIcloudCircle: ImageVector
    get() {
        if (_sFIcloudCircle != null) {
            return _sFIcloudCircle!!
        }
        _sFIcloudCircle = sfIcon(
            name = "Dualtone.SFIcloudCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M8.17383 17.3145L17.832 17.3145C19.3457 17.3145 20.4883 16.1523 20.4883 14.6875C20.4883 13.418 19.6289 12.373 18.4863 12.1387C18.5059 11.9922 18.5156 11.8164 18.5156 11.6797C18.5156 9.12109 16.5137 7.07031 13.8672 7.07031C12.3145 7.07031 10.9277 7.80273 10.0977 8.99414C9.92188 8.95508 9.6875 8.92578 9.52148 8.92578C8.22266 8.92578 7.14844 9.87305 7.00195 11.1719C5.83008 11.6309 4.94141 12.7637 4.94141 14.1602C4.94141 16.1621 6.30859 17.3145 8.17383 17.3145Z", fillAlpha = 0.85f)
        }
        return _sFIcloudCircle!!
    }

private var _sFIcloudCircle: ImageVector? = null
