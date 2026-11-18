package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPencilCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFPencilCircle: ImageVector
    get() {
        if (_sFPencilCircle != null) {
            return _sFPencilCircle!!
        }
        _sFPencilCircle = sfIcon(
            name = "Monochrome.SFPencilCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M8.55469 18.1543L17.0898 9.64844L15.752 8.31055L7.23633 16.8164L6.49414 18.4277C6.38672 18.7012 6.65039 18.9355 6.88477 18.8477ZM17.7832 8.96484L18.6133 8.125C18.9941 7.73438 19.0234 7.31445 18.6621 6.96289L18.4375 6.72852C18.0859 6.37695 17.666 6.41602 17.2852 6.79688L16.4453 7.61719Z", fillAlpha = 0.85f)
        }
        return _sFPencilCircle!!
    }

private var _sFPencilCircle: ImageVector? = null
