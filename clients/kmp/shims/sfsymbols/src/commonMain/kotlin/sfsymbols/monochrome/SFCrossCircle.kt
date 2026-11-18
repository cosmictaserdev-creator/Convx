package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCrossCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFCrossCircle: ImageVector
    get() {
        if (_sFCrossCircle != null) {
            return _sFCrossCircle!!
        }
        _sFCrossCircle = sfIcon(
            name = "Monochrome.SFCrossCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M5.84961 14.0039C5.84961 14.7559 6.36719 15.2734 7.12891 15.2734L10.127 15.2734L10.127 18.2715C10.127 19.0527 10.6348 19.5605 11.4062 19.5605L13.9941 19.5605C14.7656 19.5605 15.2734 19.0527 15.2734 18.2715L15.2734 15.2734L18.2715 15.2734C19.043 15.2734 19.5605 14.7559 19.5605 14.0039L19.5605 11.4062C19.5605 10.6445 19.043 10.127 18.2715 10.127L15.2734 10.127L15.2734 7.13867C15.2734 6.35742 14.7656 5.84961 13.9941 5.84961L11.4062 5.84961C10.6348 5.84961 10.127 6.35742 10.127 7.13867L10.127 10.127L7.12891 10.127C6.35742 10.127 5.84961 10.6445 5.84961 11.4062Z", fillAlpha = 0.85f)
        }
        return _sFCrossCircle!!
    }

private var _sFCrossCircle: ImageVector? = null
