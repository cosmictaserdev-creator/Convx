package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCapsulePortraitFill (dualtone)
 * Viewport: 22.5781 x 29.4043
 */
public val SfSymbols.Dualtone.SFCapsulePortraitFill: ImageVector
    get() {
        if (_sFCapsulePortraitFill != null) {
            return _sFCapsulePortraitFill!!
        }
        _sFCapsulePortraitFill = sfIcon(
            name = "Dualtone.SFCapsulePortraitFill",
            viewportWidth = 22.5781f,
            viewportHeight = 29.4043f
        ) {
            addSfPath("M11.1133 0C4.32617 0 0 4.16016 0 10.8594L0 18.5254C0 25.2246 4.32617 29.3848 11.1133 29.3848C17.8906 29.3848 22.2168 25.2246 22.2168 18.5254L22.2168 10.8594C22.2168 4.16016 17.8906 0 11.1133 0Z", fillAlpha = 0.85f)
        }
        return _sFCapsulePortraitFill!!
    }

private var _sFCapsulePortraitFill: ImageVector? = null
