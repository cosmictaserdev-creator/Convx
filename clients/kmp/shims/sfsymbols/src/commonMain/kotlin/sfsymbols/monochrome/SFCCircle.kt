package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFCCircle: ImageVector
    get() {
        if (_sFCCircle != null) {
            return _sFCCircle!!
        }
        _sFCCircle = sfIcon(
            name = "Monochrome.SFCCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M12.7441 18.5938C14.9512 18.5938 16.7383 17.4805 17.2754 15.7715C17.3438 15.5469 17.3633 15.4102 17.3633 15.2441C17.3633 14.7754 17.0801 14.4922 16.6211 14.4922C16.2793 14.4922 16.0547 14.6582 15.8887 15.0488C15.4395 16.3672 14.2969 17.1484 12.7637 17.1484C10.6641 17.1484 9.23828 15.332 9.23828 12.6367C9.23828 9.95117 10.6641 8.11523 12.7637 8.11523C14.2969 8.11523 15.4492 8.89648 15.8887 10.2246C16.0547 10.6055 16.2793 10.7715 16.6211 10.7715C17.0801 10.7715 17.3633 10.4883 17.3633 10.0195C17.3633 9.84375 17.3438 9.7168 17.2754 9.49219C16.7285 7.80273 14.9414 6.66992 12.7441 6.66992C9.56055 6.66992 7.5 9.02344 7.5 12.6367C7.5 16.25 9.55078 18.5938 12.7441 18.5938Z", fillAlpha = 0.85f)
        }
        return _sFCCircle!!
    }

private var _sFCCircle: ImageVector? = null
