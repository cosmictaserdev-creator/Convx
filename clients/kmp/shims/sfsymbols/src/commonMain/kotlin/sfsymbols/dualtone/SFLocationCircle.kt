package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLocationCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFLocationCircle: ImageVector
    get() {
        if (_sFLocationCircle != null) {
            return _sFLocationCircle!!
        }
        _sFLocationCircle = sfIcon(
            name = "Dualtone.SFLocationCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M5.75195 13.6621L11.2891 13.6719C11.4941 13.6719 11.6309 13.7988 11.6309 14.0137L11.6309 19.4824C11.6309 20.4785 12.6953 20.5762 13.0273 19.8535L18.7305 7.71484C19.1504 6.83594 18.4082 6.11328 17.5293 6.52344L5.40039 12.2656C4.6582 12.6172 4.79492 13.6621 5.75195 13.6621Z", fillAlpha = 0.85f)
        }
        return _sFLocationCircle!!
    }

private var _sFLocationCircle: ImageVector? = null
