package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFHomepod (dualtone)
 * Viewport: 20.3613 x 25.5957
 */
public val SfSymbols.Dualtone.SFHomepod: ImageVector
    get() {
        if (_sFHomepod != null) {
            return _sFHomepod!!
        }
        _sFHomepod = sfIcon(
            name = "Dualtone.SFHomepod",
            viewportWidth = 20.3613f,
            viewportHeight = 25.5957f
        ) {
            addSfPath("M10 25.5859C16.875 25.5859 20 23.1738 20 17.627L20 8.04688C20 5.61523 19.3164 4.04297 18.4668 3.02734C18.1738 2.67578 17.7246 2.66602 17.373 2.96875C16.2305 4.0918 13.2129 4.91211 10 4.91211C6.78711 4.91211 3.76953 4.0918 2.61719 2.96875C2.27539 2.66602 1.82617 2.67578 1.5332 3.02734C0.683594 4.04297 0 5.61523 0 8.04688L0 17.627C0 23.1738 3.11523 25.5859 10 25.5859ZM10 23.8574C4.0918 23.8574 1.72852 21.8652 1.72852 17.4414L1.72852 8.13477C1.72852 6.92383 1.96289 5.83008 2.55859 4.87305C3.98438 6.10352 6.76758 6.64062 10 6.64062C13.2324 6.64062 16.0156 6.10352 17.4414 4.87305C18.0371 5.83008 18.2715 6.92383 18.2715 8.13477L18.2715 17.4414C18.2715 21.8652 15.9082 23.8574 10 23.8574Z", fillAlpha = 0.425f)
            addSfPath("M10 3.34961C13.418 3.34961 15.9082 2.64648 15.9082 1.67969C15.9082 0.693359 13.418 0 10 0C6.58203 0 4.0918 0.693359 4.0918 1.67969C4.0918 2.64648 6.58203 3.34961 10 3.34961Z", fillAlpha = 0.85f)
        }
        return _sFHomepod!!
    }

private var _sFHomepod: ImageVector? = null
