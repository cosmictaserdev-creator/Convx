package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCheckmarkBubbleFill (dualtone)
 * Viewport: 27.8711 x 27.3926
 */
public val SfSymbols.Dualtone.SFCheckmarkBubbleFill: ImageVector
    get() {
        if (_sFCheckmarkBubbleFill != null) {
            return _sFCheckmarkBubbleFill!!
        }
        _sFCheckmarkBubbleFill = sfIcon(
            name = "Dualtone.SFCheckmarkBubbleFill",
            viewportWidth = 27.8711f,
            viewportHeight = 27.3926f
        ) {
            addSfPath("M7.2168 27.3926C7.64648 27.3926 7.96875 27.168 8.4668 26.709L13.0273 22.5977L22.002 22.5977C25.5371 22.5977 27.5098 20.5859 27.5098 17.1094L27.5098 7.14844C27.5098 3.66211 25.5371 1.64062 22.002 1.64062L5.50781 1.64062C1.96289 1.64062 0 3.65234 0 7.14844L0 17.1094C0 20.6055 1.96289 22.5977 5.50781 22.5977L6.23047 22.5977L6.23047 26.2598C6.23047 26.9434 6.5918 27.3926 7.2168 27.3926Z", fillAlpha = 0.2125f)
            addSfPath("M12.3633 18.2422C11.9922 18.2422 11.709 18.0859 11.4258 17.7246L8.0957 13.7012C7.92969 13.4863 7.83203 13.2422 7.83203 12.998C7.83203 12.5098 8.21289 12.1094 8.70117 12.1094C9.00391 12.1094 9.24805 12.2168 9.51172 12.5586L12.3242 16.0742L17.9688 7.09961C18.1641 6.77734 18.4473 6.60156 18.7402 6.60156C19.1992 6.60156 19.6387 6.93359 19.6387 7.43164C19.6387 7.66602 19.5117 7.91992 19.375 8.125L13.2617 17.7246C13.0371 18.0664 12.7344 18.2422 12.3633 18.2422Z", fillAlpha = 0.85f)
        }
        return _sFCheckmarkBubbleFill!!
    }

private var _sFCheckmarkBubbleFill: ImageVector? = null
