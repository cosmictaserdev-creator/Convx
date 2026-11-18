package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBubbleLeftFill (monochrome)
 * Viewport: 27.8711 x 27.3926
 */
public val SfSymbols.Monochrome.SFBubbleLeftFill: ImageVector
    get() {
        if (_sFBubbleLeftFill != null) {
            return _sFBubbleLeftFill!!
        }
        _sFBubbleLeftFill = sfIcon(
            name = "Monochrome.SFBubbleLeftFill",
            viewportWidth = 27.8711f,
            viewportHeight = 27.3926f
        ) {
            addSfPath("M7.2168 27.3926C7.64648 27.3926 7.96875 27.168 8.4668 26.709L13.0273 22.5977L22.002 22.5977C25.5371 22.5977 27.5098 20.5859 27.5098 17.1094L27.5098 7.14844C27.5098 3.66211 25.5371 1.64062 22.002 1.64062L5.50781 1.64062C1.96289 1.64062 0 3.65234 0 7.14844L0 17.1094C0 20.6055 1.96289 22.5977 5.50781 22.5977L6.23047 22.5977L6.23047 26.2598C6.23047 26.9434 6.5918 27.3926 7.2168 27.3926Z", fillAlpha = 0.85f)
        }
        return _sFBubbleLeftFill!!
    }

private var _sFBubbleLeftFill: ImageVector? = null
