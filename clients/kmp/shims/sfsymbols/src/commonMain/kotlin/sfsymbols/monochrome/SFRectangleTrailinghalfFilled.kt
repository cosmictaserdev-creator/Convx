package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRectangleTrailinghalfFilled (monochrome)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Monochrome.SFRectangleTrailinghalfFilled: ImageVector
    get() {
        if (_sFRectangleTrailinghalfFilled != null) {
            return _sFRectangleTrailinghalfFilled!!
        }
        _sFRectangleTrailinghalfFilled = sfIcon(
            name = "Monochrome.SFRectangleTrailinghalfFilled",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959L25.7812 22.959C28.3105 22.959 29.5898 21.6797 29.5898 19.1992L29.5898 3.76953C29.5898 1.2793 28.3105 0 25.7812 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953ZM1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L14.8145 1.72852L14.8145 21.2305L3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016Z", fillAlpha = 0.85f)
        }
        return _sFRectangleTrailinghalfFilled!!
    }

private var _sFRectangleTrailinghalfFilled: ImageVector? = null
