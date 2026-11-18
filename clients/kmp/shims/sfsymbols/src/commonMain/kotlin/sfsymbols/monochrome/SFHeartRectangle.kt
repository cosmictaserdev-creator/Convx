package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFHeartRectangle (monochrome)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Monochrome.SFHeartRectangle: ImageVector
    get() {
        if (_sFHeartRectangle != null) {
            return _sFHeartRectangle!!
        }
        _sFHeartRectangle = sfIcon(
            name = "Monochrome.SFHeartRectangle",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L25.7812 22.959C28.3105 22.959 29.5898 21.6797 29.5898 19.1992L29.5898 3.76953C29.5898 1.2793 28.3105 0 25.7812 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L25.752 1.72852C27.0898 1.72852 27.8516 2.46094 27.8516 3.85742L27.8516 19.1016C27.8516 20.5078 27.0898 21.2305 25.752 21.2305Z", fillAlpha = 0.85f)
            addSfPath("M11.7871 5.88867C9.6582 5.88867 8.125 7.5 8.125 9.69727C8.125 13.0957 11.7578 16.1719 14.2383 17.7344C14.4336 17.8613 14.6875 18.0078 14.834 18.0078C14.9707 18.0078 15.2051 17.8613 15.3809 17.7344C17.8223 16.1035 21.4941 13.0957 21.4941 9.69727C21.4941 7.5 19.9609 5.88867 17.832 5.88867C16.5039 5.88867 15.4199 6.67969 14.8145 7.80273C14.1992 6.67969 13.1348 5.88867 11.7871 5.88867Z", fillAlpha = 0.85f)
        }
        return _sFHeartRectangle!!
    }

private var _sFHeartRectangle: ImageVector? = null
