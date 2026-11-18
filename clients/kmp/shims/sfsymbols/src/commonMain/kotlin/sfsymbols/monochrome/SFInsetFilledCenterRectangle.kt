package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFInsetFilledCenterRectangle (monochrome)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Monochrome.SFInsetFilledCenterRectangle: ImageVector
    get() {
        if (_sFInsetFilledCenterRectangle != null) {
            return _sFInsetFilledCenterRectangle!!
        }
        _sFInsetFilledCenterRectangle = sfIcon(
            name = "Monochrome.SFInsetFilledCenterRectangle",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L25.7812 22.959C28.3105 22.959 29.5898 21.6797 29.5898 19.1992L29.5898 3.76953C29.5898 1.2793 28.3105 0 25.7812 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L25.752 1.72852C27.0898 1.72852 27.8516 2.46094 27.8516 3.85742L27.8516 19.1016C27.8516 20.5078 27.0898 21.2305 25.752 21.2305Z", fillAlpha = 0.85f)
            addSfPath("M7.8125 17.3828L21.7676 17.3828C22.6953 17.3828 23.0957 16.9824 23.0957 16.0352L23.0957 6.92383C23.0957 5.97656 22.6953 5.58594 21.7676 5.58594L7.8125 5.58594C6.89453 5.58594 6.49414 5.97656 6.49414 6.92383L6.49414 16.0352C6.49414 16.9824 6.89453 17.3828 7.8125 17.3828Z", fillAlpha = 0.85f)
        }
        return _sFInsetFilledCenterRectangle!!
    }

private var _sFInsetFilledCenterRectangle: ImageVector? = null
