package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFIcloudSquare (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFIcloudSquare: ImageVector
    get() {
        if (_sFIcloudSquare != null) {
            return _sFIcloudSquare!!
        }
        _sFIcloudSquare = sfIcon(
            name = "Monochrome.SFIcloudSquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.85f)
            addSfPath("M6.95312 16.0645L16.6113 16.0645C18.125 16.0645 19.2676 14.9023 19.2676 13.4375C19.2676 12.168 18.4082 11.123 17.2656 10.8887C17.2852 10.7422 17.2949 10.5664 17.2949 10.4297C17.2949 7.87109 15.293 5.82031 12.6465 5.82031C11.0938 5.82031 9.70703 6.55273 8.87695 7.74414C8.70117 7.70508 8.4668 7.67578 8.30078 7.67578C7.00195 7.67578 5.91797 8.62305 5.78125 9.92188C4.60938 10.3809 3.7207 11.5137 3.7207 12.9102C3.7207 14.9121 5.08789 16.0645 6.95312 16.0645Z", fillAlpha = 0.85f)
        }
        return _sFIcloudSquare!!
    }

private var _sFIcloudSquare: ImageVector? = null
