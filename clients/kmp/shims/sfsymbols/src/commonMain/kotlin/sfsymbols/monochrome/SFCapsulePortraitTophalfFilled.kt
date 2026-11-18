package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCapsulePortraitTophalfFilled (monochrome)
 * Viewport: 22.5781 x 29.4043
 */
public val SfSymbols.Monochrome.SFCapsulePortraitTophalfFilled: ImageVector
    get() {
        if (_sFCapsulePortraitTophalfFilled != null) {
            return _sFCapsulePortraitTophalfFilled!!
        }
        _sFCapsulePortraitTophalfFilled = sfIcon(
            name = "Monochrome.SFCapsulePortraitTophalfFilled",
            viewportWidth = 22.5781f,
            viewportHeight = 29.4043f
        ) {
            addSfPath("M11.1133 0C4.32617 0 0 4.16016 0 10.8594L0 18.5254C0 25.2246 4.32617 29.3848 11.1133 29.3848C17.8906 29.3848 22.2168 25.2246 22.2168 18.5254L22.2168 10.8594C22.2168 4.16016 17.8906 0 11.1133 0ZM1.74805 14.6973L20.4688 14.6973L20.4688 18.8281C20.4688 24.2969 16.8359 27.6465 11.1133 27.6465C5.38086 27.6465 1.74805 24.2969 1.74805 18.8281Z", fillAlpha = 0.85f)
        }
        return _sFCapsulePortraitTophalfFilled!!
    }

private var _sFCapsulePortraitTophalfFilled: ImageVector? = null
