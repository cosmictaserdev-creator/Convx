package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFLCircle: ImageVector
    get() {
        if (_sFLCircle != null) {
            return _sFLCircle!!
        }
        _sFLCircle = sfIcon(
            name = "Dualtone.SFLCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M10.293 18.3398L16.1523 18.3398C16.582 18.3398 16.875 18.0566 16.875 17.6172C16.875 17.168 16.5723 16.9043 16.1523 16.9043L11.1719 16.9043L11.1719 7.76367C11.1719 7.16797 10.8594 6.80664 10.293 6.80664C9.74609 6.80664 9.44336 7.1875 9.44336 7.76367L9.44336 17.373C9.44336 17.9395 9.74609 18.3398 10.293 18.3398Z", fillAlpha = 0.85f)
        }
        return _sFLCircle!!
    }

private var _sFLCircle: ImageVector? = null
