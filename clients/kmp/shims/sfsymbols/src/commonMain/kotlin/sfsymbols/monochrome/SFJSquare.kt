package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFJSquare (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFJSquare: ImageVector
    get() {
        if (_sFJSquare != null) {
            return _sFJSquare!!
        }
        _sFJSquare = sfIcon(
            name = "Monochrome.SFJSquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.85f)
            addSfPath("M10.918 17.3145C13.2617 17.3145 14.541 16.0059 14.541 13.7207L14.541 6.51367C14.541 5.91797 14.2383 5.55664 13.6719 5.55664C13.125 5.55664 12.8223 5.9375 12.8223 6.51367L12.8223 13.7012C12.8223 15.0195 12.1191 15.8301 11.0059 15.8301C9.98047 15.8301 9.25781 15.2734 8.98438 14.3848C8.80859 13.9648 8.56445 13.7891 8.19336 13.7891C7.71484 13.7891 7.39258 14.1113 7.39258 14.5996C7.39258 14.7754 7.42188 14.9512 7.48047 15.127C7.87109 16.4355 9.21875 17.3145 10.918 17.3145Z", fillAlpha = 0.85f)
        }
        return _sFJSquare!!
    }

private var _sFJSquare: ImageVector? = null
