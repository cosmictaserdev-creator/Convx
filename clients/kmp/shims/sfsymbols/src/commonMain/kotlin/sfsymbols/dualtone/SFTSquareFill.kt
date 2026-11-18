package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFTSquareFill: ImageVector
    get() {
        if (_sFTSquareFill != null) {
            return _sFTSquareFill!!
        }
        _sFTSquareFill = sfIcon(
            name = "Dualtone.SFTSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M11.4355 17.4023C10.8691 17.4023 10.5566 16.9922 10.5566 16.4062L10.5566 7.06055L7.29492 7.06055C6.86523 7.06055 6.54297 6.76758 6.54297 6.31836C6.54297 5.84961 6.86523 5.57617 7.29492 5.57617L15.6543 5.57617C16.0938 5.57617 16.416 5.84961 16.416 6.31836C16.416 6.76758 16.0938 7.06055 15.6543 7.06055L12.3438 7.06055L12.3438 16.4062C12.3438 17.0117 12.0215 17.4023 11.4355 17.4023Z", fillAlpha = 0.85f)
        }
        return _sFTSquareFill!!
    }

private var _sFTSquareFill: ImageVector? = null
