package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFInsetFilledBottomthirdSquare (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFInsetFilledBottomthirdSquare: ImageVector
    get() {
        if (_sFInsetFilledBottomthirdSquare != null) {
            return _sFInsetFilledBottomthirdSquare!!
        }
        _sFInsetFilledBottomthirdSquare = sfIcon(
            name = "Dualtone.SFInsetFilledBottomthirdSquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.425f)
            addSfPath("M4.51172 19.6973L18.4375 19.6973C19.2969 19.6973 19.668 19.3164 19.668 18.4473L19.668 15.8887C19.668 15.0195 19.2969 14.6387 18.4375 14.6387L4.51172 14.6387C3.66211 14.6387 3.29102 15.0195 3.29102 15.8887L3.29102 18.4473C3.29102 19.3164 3.66211 19.6973 4.51172 19.6973Z", fillAlpha = 0.85f)
        }
        return _sFInsetFilledBottomthirdSquare!!
    }

private var _sFInsetFilledBottomthirdSquare: ImageVector? = null
