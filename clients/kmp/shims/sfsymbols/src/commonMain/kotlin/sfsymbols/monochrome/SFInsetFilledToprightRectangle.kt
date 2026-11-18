package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFInsetFilledToprightRectangle (monochrome)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Monochrome.SFInsetFilledToprightRectangle: ImageVector
    get() {
        if (_sFInsetFilledToprightRectangle != null) {
            return _sFInsetFilledToprightRectangle!!
        }
        _sFInsetFilledToprightRectangle = sfIcon(
            name = "Monochrome.SFInsetFilledToprightRectangle",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L25.7812 22.959C28.3105 22.959 29.5898 21.6797 29.5898 19.1992L29.5898 3.76953C29.5898 1.2793 28.3105 0 25.7812 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L25.752 1.72852C27.0898 1.72852 27.8516 2.46094 27.8516 3.85742L27.8516 19.1016C27.8516 20.5078 27.0898 21.2305 25.752 21.2305Z", fillAlpha = 0.85f)
            addSfPath("M16.0254 11.4746L25.0684 11.4746C25.9277 11.4746 26.2988 11.0938 26.2988 10.2246L26.2988 4.54102C26.2988 3.67188 25.9277 3.29102 25.0684 3.29102L16.0254 3.29102C15.166 3.29102 14.7949 3.67188 14.7949 4.54102L14.7949 10.2246C14.7949 11.0938 15.166 11.4746 16.0254 11.4746Z", fillAlpha = 0.85f)
        }
        return _sFInsetFilledToprightRectangle!!
    }

private var _sFInsetFilledToprightRectangle: ImageVector? = null
