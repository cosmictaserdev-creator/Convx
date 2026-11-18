package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFTSquareFill: ImageVector
    get() {
        if (_sFTSquareFill != null) {
            return _sFTSquareFill!!
        }
        _sFTSquareFill = sfIcon(
            name = "Monochrome.SFTSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM7.29492 5.57617C6.86523 5.57617 6.54297 5.84961 6.54297 6.31836C6.54297 6.76758 6.86523 7.06055 7.29492 7.06055L10.5566 7.06055L10.5566 16.4062C10.5566 16.9922 10.8691 17.4023 11.4355 17.4023C12.0215 17.4023 12.3438 17.0117 12.3438 16.4062L12.3438 7.06055L15.6543 7.06055C16.0938 7.06055 16.416 6.76758 16.416 6.31836C16.416 5.84961 16.0938 5.57617 15.6543 5.57617Z", fillAlpha = 0.85f)
        }
        return _sFTSquareFill!!
    }

private var _sFTSquareFill: ImageVector? = null
