package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowDownBackwardCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFArrowDownBackwardCircle: ImageVector
    get() {
        if (_sFArrowDownBackwardCircle != null) {
            return _sFArrowDownBackwardCircle!!
        }
        _sFArrowDownBackwardCircle = sfIcon(
            name = "Monochrome.SFArrowDownBackwardCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M10.0098 16.4355L12.1191 14.4531L17.2754 9.29688C17.4316 9.14062 17.5391 8.93555 17.5391 8.71094C17.5391 8.22266 17.2168 7.91016 16.7383 7.91016C16.4746 7.91016 16.2695 8.00781 16.123 8.16406L10.9668 13.3105L9.00391 15.4102C8.20312 16.25 9.19922 17.1875 10.0098 16.4355ZM9.44336 13.0566L9.44336 10.498C9.44336 9.98047 9.13086 9.6582 8.65234 9.6582C8.17383 9.6582 7.86133 10.0098 7.86133 10.5176L7.86133 16.6797C7.86133 17.3047 8.21289 17.5586 8.75 17.5586L14.9414 17.5586C15.4492 17.5586 15.7715 17.2559 15.7715 16.7773C15.7715 16.2988 15.4395 15.9863 14.9219 15.9863L12.1582 15.9863L9.16992 16.2793Z", fillAlpha = 0.85f)
        }
        return _sFArrowDownBackwardCircle!!
    }

private var _sFArrowDownBackwardCircle: ImageVector? = null
