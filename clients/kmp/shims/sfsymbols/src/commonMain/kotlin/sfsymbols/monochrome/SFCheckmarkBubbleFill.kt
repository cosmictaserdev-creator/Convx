package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCheckmarkBubbleFill (monochrome)
 * Viewport: 27.8711 x 27.3926
 */
public val SfSymbols.Monochrome.SFCheckmarkBubbleFill: ImageVector
    get() {
        if (_sFCheckmarkBubbleFill != null) {
            return _sFCheckmarkBubbleFill!!
        }
        _sFCheckmarkBubbleFill = sfIcon(
            name = "Monochrome.SFCheckmarkBubbleFill",
            viewportWidth = 27.8711f,
            viewportHeight = 27.3926f
        ) {
            addSfPath("M27.5098 7.14844L27.5098 17.1094C27.5098 20.5859 25.5371 22.5977 22.002 22.5977L13.0273 22.5977L8.4668 26.709C7.96875 27.168 7.64648 27.3926 7.2168 27.3926C6.5918 27.3926 6.23047 26.9434 6.23047 26.2598L6.23047 22.5977L5.50781 22.5977C1.96289 22.5977 0 20.6055 0 17.1094L0 7.14844C0 3.65234 1.96289 1.64062 5.50781 1.64062L22.002 1.64062C25.5371 1.64062 27.5098 3.66211 27.5098 7.14844ZM17.9688 7.09961L12.3242 16.0742L9.51172 12.5586C9.24805 12.2168 9.00391 12.1094 8.70117 12.1094C8.21289 12.1094 7.83203 12.5098 7.83203 12.998C7.83203 13.2422 7.92969 13.4863 8.0957 13.7012L11.4258 17.7246C11.709 18.0859 11.9922 18.2422 12.3633 18.2422C12.7344 18.2422 13.0371 18.0664 13.2617 17.7246L19.375 8.125C19.5117 7.91992 19.6387 7.66602 19.6387 7.43164C19.6387 6.93359 19.1992 6.60156 18.7402 6.60156C18.4473 6.60156 18.1641 6.77734 17.9688 7.09961Z", fillAlpha = 0.85f)
        }
        return _sFCheckmarkBubbleFill!!
    }

private var _sFCheckmarkBubbleFill: ImageVector? = null
