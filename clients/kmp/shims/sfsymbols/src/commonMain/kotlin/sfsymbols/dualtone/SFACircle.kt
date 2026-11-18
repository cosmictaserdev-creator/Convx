package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFACircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFACircle: ImageVector
    get() {
        if (_sFACircle != null) {
            return _sFACircle!!
        }
        _sFACircle = sfIcon(
            name = "Dualtone.SFACircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M8.56445 18.623C8.95508 18.623 9.22852 18.4473 9.39453 17.9297L10.4004 15.0977L15 15.0977L15.9961 17.9297C16.1719 18.4375 16.4355 18.623 16.8457 18.623C17.3145 18.623 17.6367 18.3203 17.6367 17.8809C17.6367 17.7148 17.6074 17.5586 17.5195 17.3242L13.8965 7.60742C13.6914 7.05078 13.2715 6.76758 12.6953 6.76758C12.1094 6.76758 11.709 7.05078 11.5039 7.60742L7.87109 17.3242C7.79297 17.5586 7.75391 17.7148 7.75391 17.8809C7.75391 18.3301 8.07617 18.623 8.56445 18.623ZM10.8398 13.7695L12.6367 8.68164L12.7539 8.68164L14.5508 13.7695Z", fillAlpha = 0.85f)
        }
        return _sFACircle!!
    }

private var _sFACircle: ImageVector? = null
