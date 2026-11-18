package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBubbleMiddleBottomFill (monochrome)
 * Viewport: 27.8711 x 27.4707
 */
public val SfSymbols.Monochrome.SFBubbleMiddleBottomFill: ImageVector
    get() {
        if (_sFBubbleMiddleBottomFill != null) {
            return _sFBubbleMiddleBottomFill!!
        }
        _sFBubbleMiddleBottomFill = sfIcon(
            name = "Monochrome.SFBubbleMiddleBottomFill",
            viewportWidth = 27.8711f,
            viewportHeight = 27.4707f
        ) {
            addSfPath("M13.7305 27.4707C14.2285 27.4707 14.6875 27.2559 15.1172 26.5234L17.4805 22.6465L22.002 22.6465C25.5371 22.6465 27.5098 20.625 27.5098 17.1484L27.5098 7.1875C27.5098 3.70117 25.5371 1.67969 22.002 1.67969L5.50781 1.67969C1.96289 1.67969 0 3.69141 0 7.1875L0 17.1484C0 20.6445 1.96289 22.6465 5.50781 22.6465L9.98047 22.6465L12.3438 26.5234C12.7832 27.2559 13.2324 27.4707 13.7305 27.4707Z", fillAlpha = 0.85f)
        }
        return _sFBubbleMiddleBottomFill!!
    }

private var _sFBubbleMiddleBottomFill: ImageVector? = null
