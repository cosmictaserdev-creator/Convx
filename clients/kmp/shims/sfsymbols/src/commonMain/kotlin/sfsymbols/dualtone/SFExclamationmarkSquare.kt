package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFExclamationmarkSquare (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFExclamationmarkSquare: ImageVector
    get() {
        if (_sFExclamationmarkSquare != null) {
            return _sFExclamationmarkSquare!!
        }
        _sFExclamationmarkSquare = sfIcon(
            name = "Dualtone.SFExclamationmarkSquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.425f)
            addSfPath("M11.4746 13.7402C12.002 13.7402 12.3047 13.4277 12.3145 12.8516L12.4805 6.14258C12.4902 5.57617 12.0508 5.15625 11.4648 5.15625C10.8691 5.15625 10.459 5.56641 10.4688 6.13281L10.6152 12.8516C10.625 13.418 10.9375 13.7402 11.4746 13.7402ZM11.4746 17.7148C12.1484 17.7148 12.7148 17.1777 12.7148 16.5137C12.7148 15.8398 12.1582 15.3125 11.4746 15.3125C10.8008 15.3125 10.2344 15.8496 10.2344 16.5137C10.2344 17.168 10.8105 17.7148 11.4746 17.7148Z", fillAlpha = 0.85f)
        }
        return _sFExclamationmarkSquare!!
    }

private var _sFExclamationmarkSquare: ImageVector? = null
