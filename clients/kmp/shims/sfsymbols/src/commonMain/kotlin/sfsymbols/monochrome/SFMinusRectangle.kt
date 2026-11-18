package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMinusRectangle (monochrome)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Monochrome.SFMinusRectangle: ImageVector
    get() {
        if (_sFMinusRectangle != null) {
            return _sFMinusRectangle!!
        }
        _sFMinusRectangle = sfIcon(
            name = "Monochrome.SFMinusRectangle",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L25.7812 22.959C28.3105 22.959 29.5898 21.6797 29.5898 19.1992L29.5898 3.76953C29.5898 1.2793 28.3105 0 25.7812 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L25.752 1.72852C27.0898 1.72852 27.8516 2.46094 27.8516 3.85742L27.8516 19.1016C27.8516 20.5078 27.0898 21.2305 25.752 21.2305Z", fillAlpha = 0.85f)
            addSfPath("M9.63867 12.3926L19.9707 12.3926C20.5566 12.3926 20.9766 12.0605 20.9766 11.5039C20.9766 10.9277 20.5859 10.5859 19.9707 10.5859L9.63867 10.5859C9.02344 10.5859 8.62305 10.9277 8.62305 11.5039C8.62305 12.0605 9.05273 12.3926 9.63867 12.3926Z", fillAlpha = 0.85f)
        }
        return _sFMinusRectangle!!
    }

private var _sFMinusRectangle: ImageVector? = null
