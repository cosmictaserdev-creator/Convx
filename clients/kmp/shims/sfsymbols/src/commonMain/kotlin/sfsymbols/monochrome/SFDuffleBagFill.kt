package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDuffleBagFill (monochrome)
 * Viewport: 34.1504 x 24.209
 */
public val SfSymbols.Monochrome.SFDuffleBagFill: ImageVector
    get() {
        if (_sFDuffleBagFill != null) {
            return _sFDuffleBagFill!!
        }
        _sFDuffleBagFill = sfIcon(
            name = "Monochrome.SFDuffleBagFill",
            viewportWidth = 34.1504f,
            viewportHeight = 24.209f
        ) {
            addSfPath("M8.73047 8.4082L10.5859 8.4082L10.5859 5.00977C10.5859 3.02734 12.0605 1.85547 14.375 1.85547L19.4141 1.85547C21.7383 1.85547 23.2129 3.02734 23.2129 5.00977L23.2129 8.4082L25.0684 8.4082L25.0684 5.00977C25.0684 1.9043 22.8418 0 19.4141 0L14.375 0C10.957 0 8.73047 1.9043 8.73047 5.00977ZM4.00391 24.1797L29.7852 24.1797C32.2461 24.1797 33.7891 22.6562 33.7891 20.2344L33.7891 13.3105C33.7891 9.66797 31.2891 7.19727 27.627 7.19727L6.16211 7.19727C2.5 7.19727 0 9.66797 0 13.3105L0 20.2344C0 22.6562 1.54297 24.1797 4.00391 24.1797ZM7.29492 24.1797L7.29492 7.19727L8.73047 7.19727L8.73047 24.1797ZM10.5859 24.1797L10.5859 7.19727L12.0215 7.19727L12.0215 24.1797ZM12.0215 15.3711L12.0215 13.9355L21.7773 13.9355L21.7773 15.3711ZM21.7773 24.1797L21.7773 7.19727L23.2129 7.19727L23.2129 24.1797ZM25.0684 24.1797L25.0684 7.19727L26.5039 7.19727L26.5039 24.1797Z", fillAlpha = 0.85f)
        }
        return _sFDuffleBagFill!!
    }

private var _sFDuffleBagFill: ImageVector? = null
