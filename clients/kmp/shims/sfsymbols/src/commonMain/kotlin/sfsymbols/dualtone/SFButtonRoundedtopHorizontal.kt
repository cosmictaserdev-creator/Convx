package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFButtonRoundedtopHorizontal (dualtone)
 * Viewport: 28.9941 x 21.6504
 */
public val SfSymbols.Dualtone.SFButtonRoundedtopHorizontal: ImageVector
    get() {
        if (_sFButtonRoundedtopHorizontal != null) {
            return _sFButtonRoundedtopHorizontal!!
        }
        _sFButtonRoundedtopHorizontal = sfIcon(
            name = "Dualtone.SFButtonRoundedtopHorizontal",
            viewportWidth = 28.9941f,
            viewportHeight = 21.6504f
        ) {
            addSfPath("M10.2441 0C3.10547 0 0 3.31055 0 9.99023L0 17.959C0 20.3906 1.25977 21.6309 3.71094 21.6309L24.9316 21.6309C27.373 21.6309 28.6328 20.3809 28.6328 17.959L28.6328 9.99023C28.6328 3.31055 25.5371 0 18.3887 0ZM10.2637 1.73828L18.3789 1.73828C24.375 1.73828 26.9043 4.38477 26.9043 9.90234L26.9043 17.8711C26.9043 19.2188 26.1816 19.9023 24.8828 19.9023L3.75 19.9023C2.43164 19.9023 1.72852 19.2188 1.72852 17.8711L1.72852 9.90234C1.72852 4.38477 4.25781 1.73828 10.2637 1.73828Z", fillAlpha = 0.85f)
        }
        return _sFButtonRoundedtopHorizontal!!
    }

private var _sFButtonRoundedtopHorizontal: ImageVector? = null
