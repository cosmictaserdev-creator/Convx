package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPauseRectangle (dualtone)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Dualtone.SFPauseRectangle: ImageVector
    get() {
        if (_sFPauseRectangle != null) {
            return _sFPauseRectangle!!
        }
        _sFPauseRectangle = sfIcon(
            name = "Dualtone.SFPauseRectangle",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L25.7812 22.959C28.3105 22.959 29.5898 21.6797 29.5898 19.1992L29.5898 3.76953C29.5898 1.2793 28.3105 0 25.7812 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L25.752 1.72852C27.0898 1.72852 27.8516 2.46094 27.8516 3.85742L27.8516 19.1016C27.8516 20.5078 27.0898 21.2305 25.752 21.2305Z", fillAlpha = 0.425f)
            addSfPath("M11.2793 16.3379L12.5293 16.3379C13.125 16.3379 13.3984 16.0156 13.3984 15.5371L13.3984 7.38281C13.3984 6.91406 13.125 6.5918 12.5293 6.5918L11.2793 6.5918C10.6934 6.5918 10.4102 6.91406 10.4102 7.38281L10.4102 15.5371C10.4102 16.0156 10.6934 16.3379 11.2793 16.3379ZM17.0801 16.3379L18.3301 16.3379C18.9062 16.3379 19.1895 16.0156 19.1895 15.5371L19.1895 7.38281C19.1895 6.91406 18.9062 6.5918 18.3301 6.5918L17.0801 6.5918C16.4941 6.5918 16.2109 6.91406 16.2109 7.38281L16.2109 15.5371C16.2109 16.0156 16.4941 16.3379 17.0801 16.3379Z", fillAlpha = 0.85f)
        }
        return _sFPauseRectangle!!
    }

private var _sFPauseRectangle: ImageVector? = null
