package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFOvalTophalfFilled (monochrome)
 * Viewport: 32.168 x 24.0137
 */
public val SfSymbols.Monochrome.SFOvalTophalfFilled: ImageVector
    get() {
        if (_sFOvalTophalfFilled != null) {
            return _sFOvalTophalfFilled!!
        }
        _sFOvalTophalfFilled = sfIcon(
            name = "Monochrome.SFOvalTophalfFilled",
            viewportWidth = 32.168f,
            viewportHeight = 24.0137f
        ) {
            addSfPath("M0 12.002C0 18.9746 6.5918 24.0137 15.9082 24.0137C25.2148 24.0137 31.8066 18.9746 31.8066 12.002C31.8066 5.0293 25.2148 0 15.9082 0C6.5918 0 0 5.0293 0 12.002ZM1.72852 12.002L30.0781 12.002C30.0781 17.959 24.1992 22.2754 15.9082 22.2754C7.60742 22.2754 1.72852 17.959 1.72852 12.002Z", fillAlpha = 0.85f)
        }
        return _sFOvalTophalfFilled!!
    }

private var _sFOvalTophalfFilled: ImageVector? = null
