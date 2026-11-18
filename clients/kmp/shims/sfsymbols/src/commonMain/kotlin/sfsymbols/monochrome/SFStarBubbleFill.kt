package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFStarBubbleFill (monochrome)
 * Viewport: 27.8711 x 27.3926
 */
public val SfSymbols.Monochrome.SFStarBubbleFill: ImageVector
    get() {
        if (_sFStarBubbleFill != null) {
            return _sFStarBubbleFill!!
        }
        _sFStarBubbleFill = sfIcon(
            name = "Monochrome.SFStarBubbleFill",
            viewportWidth = 27.8711f,
            viewportHeight = 27.3926f
        ) {
            addSfPath("M27.5098 7.14844L27.5098 17.1094C27.5098 20.5859 25.5371 22.5977 22.002 22.5977L13.0273 22.5977L8.4668 26.709C7.96875 27.168 7.64648 27.3926 7.2168 27.3926C6.5918 27.3926 6.23047 26.9434 6.23047 26.2598L6.23047 22.5977L5.50781 22.5977C1.96289 22.5977 0 20.6055 0 17.1094L0 7.14844C0 3.65234 1.96289 1.64062 5.50781 1.64062L22.002 1.64062C25.5371 1.64062 27.5098 3.66211 27.5098 7.14844ZM13.2617 6.77734L12.1973 10.0488L8.67188 10.0488C8.08594 10.0488 7.87109 10.6445 8.37891 11.0059L11.2402 13.0566L10.1367 16.3379C9.92188 16.9727 10.4785 17.2949 10.9668 16.9434L13.7598 14.8926L16.5527 16.9434C17.041 17.2949 17.5879 16.9727 17.3828 16.3379L16.2891 13.0566L19.1406 11.0059C19.6387 10.6445 19.4238 10.0488 18.8477 10.0488L15.3223 10.0488L14.2676 6.77734C14.0918 6.2207 13.4375 6.23047 13.2617 6.77734Z", fillAlpha = 0.85f)
        }
        return _sFStarBubbleFill!!
    }

private var _sFStarBubbleFill: ImageVector? = null
