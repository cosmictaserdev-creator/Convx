package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRectangleBottomhalfFilled (monochrome)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Monochrome.SFRectangleBottomhalfFilled: ImageVector
    get() {
        if (_sFRectangleBottomhalfFilled != null) {
            return _sFRectangleBottomhalfFilled!!
        }
        _sFRectangleBottomhalfFilled = sfIcon(
            name = "Monochrome.SFRectangleBottomhalfFilled",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959L25.7812 22.959C28.3105 22.959 29.5898 21.6797 29.5898 19.1992L29.5898 3.76953C29.5898 1.2793 28.3105 0 25.7812 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953ZM1.72852 11.4746L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L25.752 1.72852C27.0898 1.72852 27.8516 2.46094 27.8516 3.85742L27.8516 11.4746Z", fillAlpha = 0.85f)
        }
        return _sFRectangleBottomhalfFilled!!
    }

private var _sFRectangleBottomhalfFilled: ImageVector? = null
