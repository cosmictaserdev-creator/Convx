package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowshapeDownCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFArrowshapeDownCircle: ImageVector
    get() {
        if (_sFArrowshapeDownCircle != null) {
            return _sFArrowshapeDownCircle!!
        }
        _sFArrowshapeDownCircle = sfIcon(
            name = "Monochrome.SFArrowshapeDownCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M15.3906 6.82617C15.3906 5.84961 14.8535 5.29297 13.9062 5.29297L11.5332 5.29297C10.5957 5.29297 10.0586 5.84961 10.0586 6.82617L10.0586 15.1953C10.0586 16.1719 10.5957 16.7285 11.5332 16.7285L13.9062 16.7285C14.8535 16.7285 15.3906 16.1719 15.3906 15.1953ZM7.13867 12.7051C6.70898 12.7051 6.40625 13.0078 6.40625 13.4277C6.40625 13.7402 6.54297 13.9844 6.79688 14.2676L11.9824 19.7852C12.2559 20.0684 12.5 20.1465 12.7148 20.1465C12.9395 20.1465 13.1836 20.0684 13.4668 19.7852L18.5938 14.2676C18.8965 13.9551 19.0234 13.75 19.0234 13.4473C19.0234 13.0078 18.6816 12.7051 18.2617 12.7051Z", fillAlpha = 0.85f)
        }
        return _sFArrowshapeDownCircle!!
    }

private var _sFArrowshapeDownCircle: ImageVector? = null
