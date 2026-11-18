package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMinusSquare (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFMinusSquare: ImageVector
    get() {
        if (_sFMinusSquare != null) {
            return _sFMinusSquare!!
        }
        _sFMinusSquare = sfIcon(
            name = "Dualtone.SFMinusSquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.425f)
            addSfPath("M6.32812 12.3926L16.6504 12.3926C17.2461 12.3926 17.666 12.0605 17.666 11.5039C17.666 10.9277 17.2656 10.5859 16.6504 10.5859L6.32812 10.5859C5.71289 10.5859 5.3125 10.9277 5.3125 11.5039C5.3125 12.0605 5.73242 12.3926 6.32812 12.3926Z", fillAlpha = 0.85f)
        }
        return _sFMinusSquare!!
    }

private var _sFMinusSquare: ImageVector? = null
