package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRectangleFillOnRectangleAngledFill (monochrome)
 * Viewport: 34.541 x 28.2129
 */
public val SfSymbols.Monochrome.SFRectangleFillOnRectangleAngledFill: ImageVector
    get() {
        if (_sFRectangleFillOnRectangleAngledFill != null) {
            return _sFRectangleFillOnRectangleAngledFill!!
        }
        _sFRectangleFillOnRectangleAngledFill = sfIcon(
            name = "Monochrome.SFRectangleFillOnRectangleAngledFill",
            viewportWidth = 34.541f,
            viewportHeight = 28.2129f
        ) {
            addSfPath("M25.918 3.70117L26.2577 5.625L11.7773 5.625C8.39844 5.625 6.42578 7.57812 6.42578 10.9375L6.42578 22.8906C6.42578 22.9108 6.42585 22.9309 6.42798 22.9495C4.74055 22.8453 3.72464 21.7946 3.37891 19.8145L1.29883 8.04688C0.869141 5.58594 1.9043 4.12109 4.39453 3.68164L21.5234 0.664062C24.0039 0.224609 25.4883 1.25977 25.918 3.70117Z", fillAlpha = 0.85f)
            addSfPath("M11.7773 26.6504L29.1797 26.6504C31.6992 26.6504 32.9785 25.3711 32.9785 22.8906L32.9785 10.9375C32.9785 8.45703 31.6992 7.17773 29.1797 7.17773L11.7773 7.17773C9.25781 7.17773 7.97852 8.44727 7.97852 10.9375L7.97852 22.8906C7.97852 25.3906 9.25781 26.6504 11.7773 26.6504Z", fillAlpha = 0.85f)
        }
        return _sFRectangleFillOnRectangleAngledFill!!
    }

private var _sFRectangleFillOnRectangleAngledFill: ImageVector? = null
