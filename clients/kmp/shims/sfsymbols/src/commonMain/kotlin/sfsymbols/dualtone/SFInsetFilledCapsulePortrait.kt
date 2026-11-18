package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFInsetFilledCapsulePortrait (dualtone)
 * Viewport: 22.5781 x 29.4043
 */
public val SfSymbols.Dualtone.SFInsetFilledCapsulePortrait: ImageVector
    get() {
        if (_sFInsetFilledCapsulePortrait != null) {
            return _sFInsetFilledCapsulePortrait!!
        }
        _sFInsetFilledCapsulePortrait = sfIcon(
            name = "Dualtone.SFInsetFilledCapsulePortrait",
            viewportWidth = 22.5781f,
            viewportHeight = 29.4043f
        ) {
            addSfPath("M11.1133 29.3848C17.8906 29.3848 22.2168 25.2246 22.2168 18.5254L22.2168 10.8594C22.2168 4.16016 17.8906 0 11.1133 0C4.32617 0 0 4.16016 0 10.8594L0 18.5254C0 25.2246 4.32617 29.3848 11.1133 29.3848ZM11.1133 27.6465C5.38086 27.6465 1.74805 24.2969 1.74805 18.8281L1.74805 10.5664C1.74805 5.08789 5.38086 1.74805 11.1133 1.74805C16.8359 1.74805 20.4688 5.08789 20.4688 10.5664L20.4688 18.8281C20.4688 24.2969 16.8359 27.6465 11.1133 27.6465Z", fillAlpha = 0.425f)
            addSfPath("M11.1133 26.084C5.98633 26.084 3.31055 23.6133 3.31055 18.8281L3.31055 10.5664C3.31055 5.78125 5.98633 3.30078 11.1133 3.30078C16.2305 3.30078 18.9062 5.78125 18.9062 10.5664L18.9062 18.8281C18.9062 23.6133 16.2305 26.084 11.1133 26.084Z", fillAlpha = 0.85f)
        }
        return _sFInsetFilledCapsulePortrait!!
    }

private var _sFInsetFilledCapsulePortrait: ImageVector? = null
