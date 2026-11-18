package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFStarBubbleFill (dualtone)
 * Viewport: 27.8711 x 27.3926
 */
public val SfSymbols.Dualtone.SFStarBubbleFill: ImageVector
    get() {
        if (_sFStarBubbleFill != null) {
            return _sFStarBubbleFill!!
        }
        _sFStarBubbleFill = sfIcon(
            name = "Dualtone.SFStarBubbleFill",
            viewportWidth = 27.8711f,
            viewportHeight = 27.3926f
        ) {
            addSfPath("M7.2168 27.3926C7.64648 27.3926 7.96875 27.168 8.4668 26.709L13.0273 22.5977L22.002 22.5977C25.5371 22.5977 27.5098 20.5859 27.5098 17.1094L27.5098 7.14844C27.5098 3.66211 25.5371 1.64062 22.002 1.64062L5.50781 1.64062C1.96289 1.64062 0 3.65234 0 7.14844L0 17.1094C0 20.6055 1.96289 22.5977 5.50781 22.5977L6.23047 22.5977L6.23047 26.2598C6.23047 26.9434 6.5918 27.3926 7.2168 27.3926Z", fillAlpha = 0.2125f)
            addSfPath("M10.9668 16.9434C10.4785 17.2949 9.92188 16.9727 10.1367 16.3379L11.2402 13.0566L8.37891 11.0059C7.87109 10.6445 8.08594 10.0488 8.67188 10.0488L12.1973 10.0488L13.2617 6.77734C13.4375 6.23047 14.0918 6.2207 14.2676 6.77734L15.3223 10.0488L18.8477 10.0488C19.4238 10.0488 19.6387 10.6445 19.1406 11.0059L16.2891 13.0566L17.3828 16.3379C17.5879 16.9727 17.041 17.2949 16.5527 16.9434L13.7598 14.8926Z", fillAlpha = 0.85f)
        }
        return _sFStarBubbleFill!!
    }

private var _sFStarBubbleFill: ImageVector? = null
