package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFChevronCompactRight (dualtone)
 * Viewport: 6.26953 x 22.1191
 */
public val SfSymbols.Dualtone.SFChevronCompactRight: ImageVector
    get() {
        if (_sFChevronCompactRight != null) {
            return _sFChevronCompactRight!!
        }
        _sFChevronCompactRight = sfIcon(
            name = "Dualtone.SFChevronCompactRight",
            viewportWidth = 6.26953f,
            viewportHeight = 22.1191f
        ) {
            addSfPath("M1.99219 21.3867L5.48828 12.5684C5.68359 12.0801 5.9082 11.4941 5.9082 11.0254C5.9082 10.5664 5.68359 9.96094 5.48828 9.48242L1.99219 0.664062C1.82617 0.253906 1.46484 0 1.08398 0C0.46875 0 0 0.458984 0 1.07422C0 1.37695 0.166016 1.79688 0.273438 2.07031L4.11133 11.7578L4.11133 10.293L0.273438 19.9805C0.166016 20.2539 0 20.6641 0 20.9863C0 21.6016 0.46875 22.0605 1.08398 22.0605C1.46484 22.0605 1.82617 21.8066 1.99219 21.3867Z", fillAlpha = 0.85f)
        }
        return _sFChevronCompactRight!!
    }

private var _sFChevronCompactRight: ImageVector? = null
