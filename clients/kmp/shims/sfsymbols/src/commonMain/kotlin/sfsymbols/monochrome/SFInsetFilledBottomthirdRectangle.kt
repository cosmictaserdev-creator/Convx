package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFInsetFilledBottomthirdRectangle (monochrome)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Monochrome.SFInsetFilledBottomthirdRectangle: ImageVector
    get() {
        if (_sFInsetFilledBottomthirdRectangle != null) {
            return _sFInsetFilledBottomthirdRectangle!!
        }
        _sFInsetFilledBottomthirdRectangle = sfIcon(
            name = "Monochrome.SFInsetFilledBottomthirdRectangle",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L25.7812 22.959C28.3105 22.959 29.5898 21.6797 29.5898 19.1992L29.5898 3.76953C29.5898 1.2793 28.3105 0 25.7812 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L25.752 1.72852C27.0898 1.72852 27.8516 2.46094 27.8516 3.85742L27.8516 19.1016C27.8516 20.5078 27.0898 21.2305 25.752 21.2305Z", fillAlpha = 0.85f)
            addSfPath("M4.51172 19.668L25.0684 19.668C25.9277 19.668 26.2988 19.2871 26.2988 18.4277L26.2988 15.8594C26.2988 14.9902 25.9277 14.6094 25.0684 14.6094L4.51172 14.6094C3.66211 14.6094 3.29102 14.9902 3.29102 15.8594L3.29102 18.4277C3.29102 19.2871 3.66211 19.668 4.51172 19.668Z", fillAlpha = 0.85f)
        }
        return _sFInsetFilledBottomthirdRectangle!!
    }

private var _sFInsetFilledBottomthirdRectangle: ImageVector? = null
