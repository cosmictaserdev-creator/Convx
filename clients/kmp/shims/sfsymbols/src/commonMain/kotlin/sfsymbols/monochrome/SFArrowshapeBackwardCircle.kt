package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowshapeBackwardCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFArrowshapeBackwardCircle: ImageVector
    get() {
        if (_sFArrowshapeBackwardCircle != null) {
            return _sFArrowshapeBackwardCircle!!
        }
        _sFArrowshapeBackwardCircle = sfIcon(
            name = "Monochrome.SFArrowshapeBackwardCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M18.291 10.0684L10.2148 10.0684C9.23828 10.0684 8.69141 10.6055 8.69141 11.5527L8.69141 13.916C8.69141 14.8633 9.23828 15.4004 10.2148 15.4004L18.291 15.4004C19.2676 15.4004 19.8242 14.8633 19.8242 13.916L19.8242 11.5527C19.8242 10.6055 19.2676 10.0684 18.291 10.0684ZM12.4121 18.3008L12.4121 7.17773C12.4121 6.75781 12.1094 6.41602 11.6699 6.41602C11.3672 6.41602 11.1621 6.54297 10.8496 6.8457L5.33203 11.9727C5.04883 12.2559 4.9707 12.5 4.9707 12.7246C4.9707 12.9297 5.04883 13.1836 5.33203 13.457L10.8496 18.6426C11.1328 18.8965 11.377 19.0332 11.6895 19.0332C12.1094 19.0332 12.4121 18.7305 12.4121 18.3008Z", fillAlpha = 0.85f)
        }
        return _sFArrowshapeBackwardCircle!!
    }

private var _sFArrowshapeBackwardCircle: ImageVector? = null
