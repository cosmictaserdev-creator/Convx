package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFEscape (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFEscape: ImageVector
    get() {
        if (_sFEscape != null) {
            return _sFEscape!!
        }
        _sFEscape = sfIcon(
            name = "Dualtone.SFEscape",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C12.2168 0 11.8066 0.419922 11.8066 0.917969C11.8066 1.42578 12.2168 1.83594 12.7148 1.83594C18.7305 1.83594 23.6035 6.70898 23.6035 12.7246C23.6035 18.7402 18.7305 23.6133 12.7148 23.6133C6.69922 23.6133 1.82617 18.7402 1.82617 12.7246C1.82617 12.2168 1.41602 11.8066 0.917969 11.8066C0.410156 11.8066 0 12.2168 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395Z", fillAlpha = 0.425f)
            addSfPath("M12.5 12.832C12.8711 12.5098 12.9883 11.9141 12.5 11.4258L5.01953 3.94531L3.13477 2.28516C2.83203 2.00195 2.36328 1.98242 2.07031 2.28516C1.77734 2.58789 1.79688 3.04688 2.07031 3.34961L3.74023 5.27344L11.1914 12.7539C11.6504 13.1934 12.168 13.125 12.5 12.832ZM1.45508 8.51562C1.93359 8.51562 2.35352 8.13477 2.32422 7.64648L2.19727 3.30078L3.05664 2.39258L7.43164 2.54883C7.92969 2.57812 8.30078 2.16797 8.31055 1.67969C8.31055 1.19141 7.92969 0.810547 7.45117 0.800781L1.66016 0.800781C0.966797 0.800781 0.576172 1.17188 0.576172 1.88477L0.576172 7.65625C0.595703 8.13477 0.986328 8.51562 1.45508 8.51562Z", fillAlpha = 0.85f)
        }
        return _sFEscape!!
    }

private var _sFEscape: ImageVector? = null
