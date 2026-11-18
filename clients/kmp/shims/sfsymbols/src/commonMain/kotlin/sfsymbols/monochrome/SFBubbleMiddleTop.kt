package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBubbleMiddleTop (monochrome)
 * Viewport: 27.8711 x 27.8613
 */
public val SfSymbols.Monochrome.SFBubbleMiddleTop: ImageVector
    get() {
        if (_sFBubbleMiddleTop != null) {
            return _sFBubbleMiddleTop!!
        }
        _sFBubbleMiddleTop = sfIcon(
            name = "Monochrome.SFBubbleMiddleTop",
            viewportWidth = 27.8711f,
            viewportHeight = 27.8613f
        ) {
            addSfPath("M13.7305 0C13.2324 0 12.7832 0.214844 12.3438 0.947266L9.98047 4.82422L5.50781 4.82422C1.96289 4.82422 0 6.82617 0 10.3223L0 20.2832C0 23.7793 1.96289 25.791 5.50781 25.791L22.002 25.791C25.5371 25.791 27.5098 23.7695 27.5098 20.2832L27.5098 10.3223C27.5098 6.8457 25.5371 4.82422 22.002 4.82422L17.4805 4.82422L15.1172 0.947266C14.6875 0.214844 14.2285 0 13.7305 0ZM13.7305 1.75781L15.9375 5.70312C16.2891 6.37695 16.543 6.55273 17.3047 6.55273L21.9922 6.55273C24.4824 6.55273 25.7812 7.90039 25.7812 10.3418L25.7812 20.2734C25.7812 22.7148 24.4824 24.0625 21.9922 24.0625L5.51758 24.0625C2.99805 24.0625 1.72852 22.7148 1.72852 20.2734L1.72852 10.3418C1.72852 7.90039 2.99805 6.55273 5.51758 6.55273L10.1562 6.55273C10.918 6.55273 11.1719 6.37695 11.5234 5.70312Z", fillAlpha = 0.85f)
        }
        return _sFBubbleMiddleTop!!
    }

private var _sFBubbleMiddleTop: ImageVector? = null
