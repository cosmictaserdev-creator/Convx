package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPoweron (monochrome)
 * Viewport: 2.1875 x 23.7012
 */
public val SfSymbols.Monochrome.SFPoweron: ImageVector
    get() {
        if (_sFPoweron != null) {
            return _sFPoweron!!
        }
        _sFPoweron = sfIcon(
            name = "Monochrome.SFPoweron",
            viewportWidth = 2.1875f,
            viewportHeight = 23.7012f
        ) {
            addSfPath("M0.917969 23.7012C1.41602 23.7012 1.82617 23.3105 1.82617 22.8027L1.82617 0.908203C1.82617 0.410156 1.41602 0.0195312 0.917969 0.0195312C0.410156 0.0195312 0 0.410156 0 0.908203L0 22.8027C0 23.3105 0.410156 23.7012 0.917969 23.7012Z", fillAlpha = 0.85f)
        }
        return _sFPoweron!!
    }

private var _sFPoweron: ImageVector? = null
