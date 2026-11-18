package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCapsulePortraitLefthalfFilled (dualtone)
 * Viewport: 22.5781 x 29.4043
 */
public val SfSymbols.Dualtone.SFCapsulePortraitLefthalfFilled: ImageVector
    get() {
        if (_sFCapsulePortraitLefthalfFilled != null) {
            return _sFCapsulePortraitLefthalfFilled!!
        }
        _sFCapsulePortraitLefthalfFilled = sfIcon(
            name = "Dualtone.SFCapsulePortraitLefthalfFilled",
            viewportWidth = 22.5781f,
            viewportHeight = 29.4043f
        ) {
            addSfPath("M11.1133 0C4.32617 0 0 4.16016 0 10.8594L0 18.5254C0 25.2246 4.32617 29.3848 11.1133 29.3848C17.8906 29.3848 22.2168 25.2246 22.2168 18.5254L22.2168 10.8594C22.2168 4.16016 17.8906 0 11.1133 0ZM11.1133 1.74805C16.8359 1.74805 20.4688 5.08789 20.4688 10.5664L20.4688 18.8281C20.4688 24.2969 16.8359 27.6465 11.1133 27.6465Z", fillAlpha = 0.85f)
        }
        return _sFCapsulePortraitLefthalfFilled!!
    }

private var _sFCapsulePortraitLefthalfFilled: ImageVector? = null
