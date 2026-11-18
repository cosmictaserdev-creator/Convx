package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFButtonRoundedtopHorizontalFill (dualtone)
 * Viewport: 28.9941 x 21.6504
 */
public val SfSymbols.Dualtone.SFButtonRoundedtopHorizontalFill: ImageVector
    get() {
        if (_sFButtonRoundedtopHorizontalFill != null) {
            return _sFButtonRoundedtopHorizontalFill!!
        }
        _sFButtonRoundedtopHorizontalFill = sfIcon(
            name = "Dualtone.SFButtonRoundedtopHorizontalFill",
            viewportWidth = 28.9941f,
            viewportHeight = 21.6504f
        ) {
            addSfPath("M10.2441 0C3.10547 0 0 3.31055 0 9.99023L0 17.959C0 20.3906 1.25977 21.6309 3.71094 21.6309L24.9316 21.6309C27.373 21.6309 28.6328 20.3809 28.6328 17.959L28.6328 9.99023C28.6328 3.31055 25.5371 0 18.3887 0Z", fillAlpha = 0.85f)
        }
        return _sFButtonRoundedtopHorizontalFill!!
    }

private var _sFButtonRoundedtopHorizontalFill: ImageVector? = null
