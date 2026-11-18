package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSquareshape (monochrome)
 * Viewport: 23.3203 x 22.9785
 */
public val SfSymbols.Monochrome.SFSquareshape: ImageVector
    get() {
        if (_sFSquareshape != null) {
            return _sFSquareshape!!
        }
        _sFSquareshape = sfIcon(
            name = "Monochrome.SFSquareshape",
            viewportWidth = 23.3203f,
            viewportHeight = 22.9785f
        ) {
            addSfPath("M0 22.0508C0 22.5977 0.361328 22.959 0.908203 22.959L22.0508 22.959C22.5977 22.959 22.959 22.5977 22.959 22.0508L22.959 0.908203C22.959 0.361328 22.5977 0 22.0508 0L0.908203 0C0.361328 0 0 0.361328 0 0.908203ZM1.72852 20.8887L1.72852 2.07031C1.72852 1.82617 1.82617 1.72852 2.07031 1.72852L20.8887 1.72852C21.1328 1.72852 21.2305 1.82617 21.2305 2.07031L21.2305 20.8887C21.2305 21.1328 21.1328 21.2305 20.8887 21.2305L2.07031 21.2305C1.82617 21.2305 1.72852 21.1328 1.72852 20.8887Z", fillAlpha = 0.85f)
        }
        return _sFSquareshape!!
    }

private var _sFSquareshape: ImageVector? = null
