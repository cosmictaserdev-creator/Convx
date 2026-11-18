package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFInsetFilledTophalfRectangle (dualtone)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Dualtone.SFInsetFilledTophalfRectangle: ImageVector
    get() {
        if (_sFInsetFilledTophalfRectangle != null) {
            return _sFInsetFilledTophalfRectangle!!
        }
        _sFInsetFilledTophalfRectangle = sfIcon(
            name = "Dualtone.SFInsetFilledTophalfRectangle",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L25.7812 22.959C28.3105 22.959 29.5898 21.6797 29.5898 19.1992L29.5898 3.76953C29.5898 1.2793 28.3105 0 25.7812 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L25.752 1.72852C27.0898 1.72852 27.8516 2.46094 27.8516 3.85742L27.8516 19.1016C27.8516 20.5078 27.0898 21.2305 25.752 21.2305Z", fillAlpha = 0.425f)
            addSfPath("M4.51172 11.4551L25.0684 11.4551C25.8594 11.4551 26.2988 11.0059 26.2988 10.2148L26.2988 4.54102C26.2988 3.74023 25.8594 3.29102 25.0684 3.29102L4.51172 3.29102C3.73047 3.29102 3.29102 3.74023 3.29102 4.54102L3.29102 10.2148C3.29102 11.0059 3.73047 11.4551 4.51172 11.4551Z", fillAlpha = 0.85f)
        }
        return _sFInsetFilledTophalfRectangle!!
    }

private var _sFInsetFilledTophalfRectangle: ImageVector? = null
