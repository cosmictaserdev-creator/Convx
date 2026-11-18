package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowUpBackwardCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFArrowUpBackwardCircle: ImageVector
    get() {
        if (_sFArrowUpBackwardCircle != null) {
            return _sFArrowUpBackwardCircle!!
        }
        _sFArrowUpBackwardCircle = sfIcon(
            name = "Monochrome.SFArrowUpBackwardCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M10.0098 9.02344C9.19922 8.27148 8.20312 9.19922 9.00391 10.0488L10.9668 12.1484L16.123 17.2949C16.2695 17.4414 16.4746 17.5391 16.7383 17.5391C17.2168 17.5391 17.5391 17.2266 17.5391 16.7383C17.5391 16.5137 17.4316 16.3086 17.2754 16.1523L12.1191 10.9961ZM9.44336 12.4023L9.16992 9.16992L12.1582 9.47266L14.9219 9.47266C15.4395 9.47266 15.7715 9.15039 15.7715 8.67188C15.7715 8.20312 15.4492 7.89062 14.9414 7.89062L8.75 7.89062C8.21289 7.89062 7.86133 8.14453 7.86133 8.7793L7.86133 14.9316C7.86133 15.4395 8.17383 15.791 8.65234 15.791C9.13086 15.791 9.44336 15.4688 9.44336 14.9512Z", fillAlpha = 0.85f)
        }
        return _sFArrowUpBackwardCircle!!
    }

private var _sFArrowUpBackwardCircle: ImageVector? = null
