package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCheckmarkRectangle (dualtone)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Dualtone.SFCheckmarkRectangle: ImageVector
    get() {
        if (_sFCheckmarkRectangle != null) {
            return _sFCheckmarkRectangle!!
        }
        _sFCheckmarkRectangle = sfIcon(
            name = "Dualtone.SFCheckmarkRectangle",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L25.7812 22.959C28.3105 22.959 29.5898 21.6797 29.5898 19.1992L29.5898 3.76953C29.5898 1.2793 28.3105 0 25.7812 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L25.752 1.72852C27.0898 1.72852 27.8516 2.46094 27.8516 3.85742L27.8516 19.1016C27.8516 20.5078 27.0898 21.2305 25.752 21.2305Z", fillAlpha = 0.425f)
            addSfPath("M13.4375 17.4512C13.7891 17.4512 14.082 17.2852 14.2969 16.9531L20.293 7.56836C20.4102 7.37305 20.5469 7.12891 20.5469 6.89453C20.5469 6.41602 20.1172 6.09375 19.668 6.09375C19.3848 6.09375 19.1113 6.26953 18.916 6.58203L13.3887 15.3613L10.6348 11.9141C10.3906 11.582 10.1465 11.4844 9.84375 11.4844C9.375 11.4844 9.01367 11.8652 9.01367 12.3438C9.01367 12.5781 9.11133 12.8125 9.25781 13.0176L12.5195 16.9531C12.793 17.3047 13.0762 17.4512 13.4375 17.4512Z", fillAlpha = 0.85f)
        }
        return _sFCheckmarkRectangle!!
    }

private var _sFCheckmarkRectangle: ImageVector? = null
