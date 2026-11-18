package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBubbleMiddleTopFill (monochrome)
 * Viewport: 27.8711 x 27.4512
 */
public val SfSymbols.Monochrome.SFBubbleMiddleTopFill: ImageVector
    get() {
        if (_sFBubbleMiddleTopFill != null) {
            return _sFBubbleMiddleTopFill!!
        }
        _sFBubbleMiddleTopFill = sfIcon(
            name = "Monochrome.SFBubbleMiddleTopFill",
            viewportWidth = 27.8711f,
            viewportHeight = 27.4512f
        ) {
            addSfPath("M13.7305 0C13.2324 0 12.7832 0.224609 12.3438 0.957031L9.98047 4.82422L5.50781 4.82422C1.96289 4.82422 0 6.83594 0 10.332L0 20.293C0 23.7891 1.96289 25.791 5.50781 25.791L22.002 25.791C25.5371 25.791 27.5098 23.7793 27.5098 20.293L27.5098 10.332C27.5098 6.8457 25.5371 4.82422 22.002 4.82422L17.4805 4.82422L15.1172 0.957031C14.6875 0.224609 14.2285 0 13.7305 0Z", fillAlpha = 0.85f)
        }
        return _sFBubbleMiddleTopFill!!
    }

private var _sFBubbleMiddleTopFill: ImageVector? = null
