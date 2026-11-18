package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFHCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFHCircle: ImageVector
    get() {
        if (_sFHCircle != null) {
            return _sFHCircle!!
        }
        _sFHCircle = sfIcon(
            name = "Monochrome.SFHCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M8.76953 18.4668C9.32617 18.4668 9.63867 18.1152 9.63867 17.5195L9.63867 13.125L15.7617 13.125L15.7617 17.5195C15.7617 18.1055 16.084 18.4668 16.6113 18.4668C17.168 18.4668 17.4805 18.1152 17.4805 17.5195L17.4805 7.75391C17.4805 7.14844 17.168 6.80664 16.6113 6.80664C16.084 6.80664 15.7617 7.1582 15.7617 7.75391L15.7617 11.7578L9.63867 11.7578L9.63867 7.75391C9.63867 7.14844 9.32617 6.80664 8.76953 6.80664C8.24219 6.80664 7.91016 7.1582 7.91016 7.75391L7.91016 17.5195C7.91016 18.1055 8.24219 18.4668 8.76953 18.4668Z", fillAlpha = 0.85f)
        }
        return _sFHCircle!!
    }

private var _sFHCircle: ImageVector? = null
