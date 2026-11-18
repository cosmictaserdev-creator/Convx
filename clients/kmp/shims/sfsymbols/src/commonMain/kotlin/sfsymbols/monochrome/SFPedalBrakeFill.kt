package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPedalBrakeFill (monochrome)
 * Viewport: 25.2051 x 21.9824
 */
public val SfSymbols.Monochrome.SFPedalBrakeFill: ImageVector
    get() {
        if (_sFPedalBrakeFill != null) {
            return _sFPedalBrakeFill!!
        }
        _sFPedalBrakeFill = sfIcon(
            name = "Monochrome.SFPedalBrakeFill",
            viewportWidth = 25.2051f,
            viewportHeight = 21.9824f
        ) {
            addSfPath("M24.502 6.43555C24.9414 10.166 24.9414 12.8906 24.6191 15.6543C24.1113 19.9902 22.7246 21.9824 18.5938 21.9824L3.80859 21.9824C1.39648 21.9824 0 20.5859 0 18.2031L0 6.49414C0 3.81836 1.50391 2.2168 4.375 1.89453L16.582 0.517578C21.5137-0.0195312 23.9844 1.81641 24.502 6.43555ZM5.55664 15.4395C5.14648 15.4395 4.81445 15.7812 4.81445 16.1816C4.81445 16.5918 5.14648 16.9238 5.55664 16.9238L19.1406 16.9238C19.5605 16.9238 19.8926 16.5918 19.8926 16.1816C19.8926 15.7812 19.5605 15.4395 19.1406 15.4395ZM5.55664 11.1621C5.14648 11.1621 4.81445 11.4941 4.81445 11.9043C4.81445 12.3047 5.14648 12.6367 5.55664 12.6367L19.1406 12.6367C19.5605 12.6367 19.8926 12.3047 19.8926 11.9043C19.8926 11.4941 19.5605 11.1621 19.1406 11.1621ZM5.55664 6.875C5.14648 6.875 4.81445 7.20703 4.81445 7.61719C4.81445 8.02734 5.14648 8.35938 5.55664 8.35938L19.1406 8.35938C19.5605 8.35938 19.8926 8.02734 19.8926 7.61719C19.8926 7.20703 19.5605 6.875 19.1406 6.875Z", fillAlpha = 0.85f)
        }
        return _sFPedalBrakeFill!!
    }

private var _sFPedalBrakeFill: ImageVector? = null
