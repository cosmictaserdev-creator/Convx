package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCloudCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFCloudCircle: ImageVector
    get() {
        if (_sFCloudCircle != null) {
            return _sFCloudCircle!!
        }
        _sFCloudCircle = sfIcon(
            name = "Dualtone.SFCloudCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M8.08594 17.3145L16.6309 17.3145C18.8086 17.3145 20.498 15.6738 20.498 13.584C20.498 11.4453 18.75 9.86328 16.4258 9.88281C15.5762 8.13477 13.9941 7.07031 12.0215 7.07031C9.48242 7.07031 7.38281 9.07227 7.16797 11.6309C5.86914 11.9922 4.94141 13.0762 4.94141 14.4629C4.94141 16.1133 6.17188 17.3145 8.08594 17.3145Z", fillAlpha = 0.85f)
        }
        return _sFCloudCircle!!
    }

private var _sFCloudCircle: ImageVector? = null
