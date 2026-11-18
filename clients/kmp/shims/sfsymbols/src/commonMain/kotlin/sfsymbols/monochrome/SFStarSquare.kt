package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFStarSquare (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFStarSquare: ImageVector
    get() {
        if (_sFStarSquare != null) {
            return _sFStarSquare!!
        }
        _sFStarSquare = sfIcon(
            name = "Monochrome.SFStarSquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.85f)
            addSfPath("M7.79297 17.9492L11.4844 15.2539L15.1758 17.9492C15.8203 18.4375 16.5137 17.9395 16.2598 17.168L14.8047 12.8125L18.5352 10.1562C19.1016 9.73633 18.9355 8.85742 18.1152 8.86719L13.5449 8.89648L12.1484 4.52148C11.9141 3.78906 11.0547 3.78906 10.8203 4.52148L9.42383 8.89648L4.85352 8.86719C4.04297 8.85742 3.83789 9.7168 4.44336 10.166L8.16406 12.8125L6.71875 17.168C6.46484 17.9395 7.1582 18.4375 7.79297 17.9492Z", fillAlpha = 0.85f)
        }
        return _sFStarSquare!!
    }

private var _sFStarSquare: ImageVector? = null
