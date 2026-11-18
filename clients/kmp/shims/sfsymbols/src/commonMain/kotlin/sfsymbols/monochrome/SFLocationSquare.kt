package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLocationSquare (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFLocationSquare: ImageVector
    get() {
        if (_sFLocationSquare != null) {
            return _sFLocationSquare!!
        }
        _sFLocationSquare = sfIcon(
            name = "Monochrome.SFLocationSquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.85f)
            addSfPath("M4.80469 12.4121L10.3418 12.4219C10.5469 12.4219 10.6836 12.5488 10.6836 12.7637L10.6836 18.2324C10.6836 19.2285 11.748 19.3262 12.0801 18.6035L17.7832 6.46484C18.2031 5.58594 17.4609 4.86328 16.582 5.27344L4.45312 11.0156C3.71094 11.3672 3.84766 12.4121 4.80469 12.4121Z", fillAlpha = 0.85f)
        }
        return _sFLocationSquare!!
    }

private var _sFLocationSquare: ImageVector? = null
