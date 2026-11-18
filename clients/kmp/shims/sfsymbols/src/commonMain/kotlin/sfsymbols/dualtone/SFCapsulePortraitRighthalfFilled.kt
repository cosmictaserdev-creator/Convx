package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCapsulePortraitRighthalfFilled (dualtone)
 * Viewport: 22.5781 x 29.4043
 */
public val SfSymbols.Dualtone.SFCapsulePortraitRighthalfFilled: ImageVector
    get() {
        if (_sFCapsulePortraitRighthalfFilled != null) {
            return _sFCapsulePortraitRighthalfFilled!!
        }
        _sFCapsulePortraitRighthalfFilled = sfIcon(
            name = "Dualtone.SFCapsulePortraitRighthalfFilled",
            viewportWidth = 22.5781f,
            viewportHeight = 29.4043f
        ) {
            addSfPath("M11.1133 0C4.32617 0 0 4.16016 0 10.8594L0 18.5254C0 25.2246 4.32617 29.3848 11.1133 29.3848C17.8906 29.3848 22.2168 25.2246 22.2168 18.5254L22.2168 10.8594C22.2168 4.16016 17.8906 0 11.1133 0ZM11.1133 1.74805L11.1133 27.6465C5.38086 27.6465 1.74805 24.2969 1.74805 18.8281L1.74805 10.5664C1.74805 5.08789 5.38086 1.74805 11.1133 1.74805Z", fillAlpha = 0.85f)
        }
        return _sFCapsulePortraitRighthalfFilled!!
    }

private var _sFCapsulePortraitRighthalfFilled: ImageVector? = null
