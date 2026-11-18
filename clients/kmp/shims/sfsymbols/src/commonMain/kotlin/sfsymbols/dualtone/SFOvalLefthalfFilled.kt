package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFOvalLefthalfFilled (dualtone)
 * Viewport: 32.168 x 24.0137
 */
public val SfSymbols.Dualtone.SFOvalLefthalfFilled: ImageVector
    get() {
        if (_sFOvalLefthalfFilled != null) {
            return _sFOvalLefthalfFilled!!
        }
        _sFOvalLefthalfFilled = sfIcon(
            name = "Dualtone.SFOvalLefthalfFilled",
            viewportWidth = 32.168f,
            viewportHeight = 24.0137f
        ) {
            addSfPath("M0 12.002C0 18.9746 6.5918 24.0137 15.9082 24.0137C25.2148 24.0137 31.8066 18.9746 31.8066 12.002C31.8066 5.0293 25.2148 0 15.9082 0C6.5918 0 0 5.0293 0 12.002ZM15.9082 22.2754L15.9082 1.73828C24.1992 1.73828 30.0781 6.05469 30.0781 12.002C30.0781 17.959 24.1992 22.2754 15.9082 22.2754Z", fillAlpha = 0.85f)
        }
        return _sFOvalLefthalfFilled!!
    }

private var _sFOvalLefthalfFilled: ImageVector? = null
