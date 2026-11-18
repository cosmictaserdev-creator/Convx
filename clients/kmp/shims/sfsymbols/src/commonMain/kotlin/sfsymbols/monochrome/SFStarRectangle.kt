package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFStarRectangle (monochrome)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Monochrome.SFStarRectangle: ImageVector
    get() {
        if (_sFStarRectangle != null) {
            return _sFStarRectangle!!
        }
        _sFStarRectangle = sfIcon(
            name = "Monochrome.SFStarRectangle",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L25.7812 22.959C28.3105 22.959 29.5898 21.6797 29.5898 19.1992L29.5898 3.76953C29.5898 1.2793 28.3105 0 25.7812 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L25.752 1.72852C27.0898 1.72852 27.8516 2.46094 27.8516 3.85742L27.8516 19.1016C27.8516 20.5078 27.0898 21.2305 25.752 21.2305Z", fillAlpha = 0.85f)
            addSfPath("M11.1133 17.9492L14.7949 15.2539L18.4863 17.9492C19.1406 18.4375 19.834 17.9395 19.5801 17.168L18.125 12.8125L21.8457 10.1562C22.4219 9.73633 22.2559 8.85742 21.4258 8.86719L16.8555 8.89648L15.459 4.52148C15.2246 3.78906 14.375 3.78906 14.1406 4.52148L12.7441 8.89648L8.17383 8.86719C7.36328 8.85742 7.1582 9.7168 7.75391 10.166L11.4746 12.8125L10.0293 17.168C9.77539 17.9395 10.4688 18.4375 11.1133 17.9492Z", fillAlpha = 0.85f)
        }
        return _sFStarRectangle!!
    }

private var _sFStarRectangle: ImageVector? = null
