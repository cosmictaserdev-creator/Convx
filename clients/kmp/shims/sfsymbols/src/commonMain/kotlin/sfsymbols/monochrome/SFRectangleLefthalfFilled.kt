package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRectangleLefthalfFilled (monochrome)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Monochrome.SFRectangleLefthalfFilled: ImageVector
    get() {
        if (_sFRectangleLefthalfFilled != null) {
            return _sFRectangleLefthalfFilled!!
        }
        _sFRectangleLefthalfFilled = sfIcon(
            name = "Monochrome.SFRectangleLefthalfFilled",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959L25.7812 22.959C28.3105 22.959 29.5898 21.6797 29.5898 19.1992L29.5898 3.76953C29.5898 1.2793 28.3105 0 25.7812 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953ZM14.8145 21.2305L14.8145 1.72852L25.752 1.72852C27.0898 1.72852 27.8516 2.46094 27.8516 3.85742L27.8516 19.1016C27.8516 20.5078 27.0898 21.2305 25.752 21.2305Z", fillAlpha = 0.85f)
        }
        return _sFRectangleLefthalfFilled!!
    }

private var _sFRectangleLefthalfFilled: ImageVector? = null
