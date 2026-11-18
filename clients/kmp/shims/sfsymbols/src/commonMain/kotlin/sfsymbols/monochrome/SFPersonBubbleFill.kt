package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPersonBubbleFill (monochrome)
 * Viewport: 27.8711 x 27.3926
 */
public val SfSymbols.Monochrome.SFPersonBubbleFill: ImageVector
    get() {
        if (_sFPersonBubbleFill != null) {
            return _sFPersonBubbleFill!!
        }
        _sFPersonBubbleFill = sfIcon(
            name = "Monochrome.SFPersonBubbleFill",
            viewportWidth = 27.8711f,
            viewportHeight = 27.3926f
        ) {
            addSfPath("M27.5098 7.14844L27.5098 17.1094C27.5098 20.5859 25.5371 22.5977 22.002 22.5977L13.0273 22.5977L8.4668 26.709C7.96875 27.168 7.64648 27.3926 7.2168 27.3926C6.5918 27.3926 6.23047 26.9434 6.23047 26.2598L6.23047 22.5977L5.50781 22.5977C1.96289 22.5977 0 20.6055 0 17.1094L0 7.14844C0 3.65234 1.96289 1.64062 5.50781 1.64062L22.002 1.64062C25.5371 1.64062 27.5098 3.66211 27.5098 7.14844ZM7.33398 17.959C7.33398 18.3789 7.5293 18.6719 7.98828 18.6719L19.541 18.6719C20 18.6719 20.1953 18.3789 20.1953 17.959C20.1953 16.5918 18.1445 12.9785 13.7598 12.9785C9.38477 12.9785 7.33398 16.5918 7.33398 17.959ZM10.5664 8.45703C10.5664 10.459 11.9922 11.9531 13.7598 11.9629C15.5371 11.9727 16.9629 10.459 16.9629 8.45703C16.9629 6.5918 15.5371 5.03906 13.7598 5.03906C11.9922 5.03906 10.5664 6.5918 10.5664 8.45703Z", fillAlpha = 0.85f)
        }
        return _sFPersonBubbleFill!!
    }

private var _sFPersonBubbleFill: ImageVector? = null
