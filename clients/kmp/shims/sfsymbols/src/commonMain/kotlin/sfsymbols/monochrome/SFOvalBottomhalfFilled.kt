package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFOvalBottomhalfFilled (monochrome)
 * Viewport: 32.168 x 24.0137
 */
public val SfSymbols.Monochrome.SFOvalBottomhalfFilled: ImageVector
    get() {
        if (_sFOvalBottomhalfFilled != null) {
            return _sFOvalBottomhalfFilled!!
        }
        _sFOvalBottomhalfFilled = sfIcon(
            name = "Monochrome.SFOvalBottomhalfFilled",
            viewportWidth = 32.168f,
            viewportHeight = 24.0137f
        ) {
            addSfPath("M0 12.0117C0 18.9844 6.5918 24.0137 15.9082 24.0137C25.2148 24.0137 31.8066 18.9844 31.8066 12.0117C31.8066 5.03906 25.2148 0 15.9082 0C6.5918 0 0 5.03906 0 12.0117ZM1.72852 12.0117C1.72852 6.05469 7.60742 1.73828 15.9082 1.73828C24.1992 1.73828 30.0781 6.05469 30.0781 12.0117Z", fillAlpha = 0.85f)
        }
        return _sFOvalBottomhalfFilled!!
    }

private var _sFOvalBottomhalfFilled: ImageVector? = null
