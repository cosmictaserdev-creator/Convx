package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBubbleRightFill (monochrome)
 * Viewport: 27.8711 x 27.3926
 */
public val SfSymbols.Monochrome.SFBubbleRightFill: ImageVector
    get() {
        if (_sFBubbleRightFill != null) {
            return _sFBubbleRightFill!!
        }
        _sFBubbleRightFill = sfIcon(
            name = "Monochrome.SFBubbleRightFill",
            viewportWidth = 27.8711f,
            viewportHeight = 27.3926f
        ) {
            addSfPath("M20.293 27.3926C20.918 27.3926 21.2793 26.9434 21.2793 26.2598L21.2793 22.5977L22.002 22.5977C25.5371 22.5977 27.5098 20.6055 27.5098 17.1094L27.5098 7.14844C27.5098 3.65234 25.5371 1.64062 22.002 1.64062L5.50781 1.64062C1.96289 1.64062 0 3.66211 0 7.14844L0 17.1094C0 20.5859 1.96289 22.5977 5.50781 22.5977L14.4727 22.5977L19.043 26.709C19.541 27.168 19.8535 27.3926 20.293 27.3926Z", fillAlpha = 0.85f)
        }
        return _sFBubbleRightFill!!
    }

private var _sFBubbleRightFill: ImageVector? = null
