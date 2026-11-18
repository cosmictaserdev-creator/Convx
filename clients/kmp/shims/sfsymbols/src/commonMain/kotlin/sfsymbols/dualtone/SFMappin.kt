package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMappin (dualtone)
 * Viewport: 9.19922 x 26.8457
 */
public val SfSymbols.Dualtone.SFMappin: ImageVector
    get() {
        if (_sFMappin != null) {
            return _sFMappin!!
        }
        _sFMappin = sfIcon(
            name = "Dualtone.SFMappin",
            viewportWidth = 9.19922f,
            viewportHeight = 26.8457f
        ) {
            addSfPath("M8.83789 4.43359C8.83789 6.54297 7.37305 8.31055 5.39062 8.74023L5.39062 21.2598C5.39062 24.4434 4.85352 26.1426 4.41406 26.1426C3.98438 26.1426 3.42773 24.4238 3.42773 21.2598L3.42773 8.74023C1.45508 8.30078 0 6.54297 0 4.43359C0 1.99219 1.96289 0 4.41406 0C6.875 0 8.83789 1.99219 8.83789 4.43359ZM1.61133 3.125C1.61133 3.94531 2.32422 4.64844 3.125 4.64844C3.95508 4.64844 4.63867 3.94531 4.63867 3.125C4.63867 2.30469 3.95508 1.61133 3.125 1.61133C2.32422 1.61133 1.61133 2.30469 1.61133 3.125Z", fillAlpha = 0.85f)
        }
        return _sFMappin!!
    }

private var _sFMappin: ImageVector? = null
