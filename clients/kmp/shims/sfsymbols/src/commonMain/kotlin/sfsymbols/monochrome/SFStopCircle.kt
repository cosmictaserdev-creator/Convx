package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFStopCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFStopCircle: ImageVector
    get() {
        if (_sFStopCircle != null) {
            return _sFStopCircle!!
        }
        _sFStopCircle = sfIcon(
            name = "Monochrome.SFStopCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M9.3457 17.2852L16.0645 17.2852C16.8164 17.2852 17.2754 16.8457 17.2754 16.1035L17.2754 9.33594C17.2754 8.60352 16.8164 8.1543 16.0645 8.1543L9.3457 8.1543C8.59375 8.1543 8.14453 8.60352 8.14453 9.33594L8.14453 16.1035C8.14453 16.8457 8.59375 17.2852 9.3457 17.2852Z", fillAlpha = 0.85f)
        }
        return _sFStopCircle!!
    }

private var _sFStopCircle: ImageVector? = null
