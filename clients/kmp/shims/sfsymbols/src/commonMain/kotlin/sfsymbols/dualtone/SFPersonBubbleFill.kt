package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPersonBubbleFill (dualtone)
 * Viewport: 27.8711 x 27.3926
 */
public val SfSymbols.Dualtone.SFPersonBubbleFill: ImageVector
    get() {
        if (_sFPersonBubbleFill != null) {
            return _sFPersonBubbleFill!!
        }
        _sFPersonBubbleFill = sfIcon(
            name = "Dualtone.SFPersonBubbleFill",
            viewportWidth = 27.8711f,
            viewportHeight = 27.3926f
        ) {
            addSfPath("M7.2168 27.3926C7.64648 27.3926 7.96875 27.168 8.4668 26.709L13.0273 22.5977L22.002 22.5977C25.5371 22.5977 27.5098 20.5859 27.5098 17.1094L27.5098 7.14844C27.5098 3.66211 25.5371 1.64062 22.002 1.64062L5.50781 1.64062C1.96289 1.64062 0 3.65234 0 7.14844L0 17.1094C0 20.6055 1.96289 22.5977 5.50781 22.5977L6.23047 22.5977L6.23047 26.2598C6.23047 26.9434 6.5918 27.3926 7.2168 27.3926Z", fillAlpha = 0.2125f)
            addSfPath("M7.98828 18.6719C7.5293 18.6719 7.33398 18.3789 7.33398 17.959C7.33398 16.5918 9.38477 12.9785 13.7598 12.9785C18.1445 12.9785 20.1953 16.5918 20.1953 17.959C20.1953 18.3789 20 18.6719 19.541 18.6719ZM13.7598 11.9629C11.9922 11.9531 10.5664 10.459 10.5664 8.45703C10.5664 6.5918 11.9922 5.03906 13.7598 5.03906C15.5371 5.03906 16.9629 6.5918 16.9629 8.45703C16.9629 10.459 15.5371 11.9727 13.7598 11.9629Z", fillAlpha = 0.85f)
        }
        return _sFPersonBubbleFill!!
    }

private var _sFPersonBubbleFill: ImageVector? = null
