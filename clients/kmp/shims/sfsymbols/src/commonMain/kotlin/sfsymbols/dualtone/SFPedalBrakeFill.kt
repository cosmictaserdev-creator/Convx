package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPedalBrakeFill (dualtone)
 * Viewport: 25.2051 x 21.9824
 */
public val SfSymbols.Dualtone.SFPedalBrakeFill: ImageVector
    get() {
        if (_sFPedalBrakeFill != null) {
            return _sFPedalBrakeFill!!
        }
        _sFPedalBrakeFill = sfIcon(
            name = "Dualtone.SFPedalBrakeFill",
            viewportWidth = 25.2051f,
            viewportHeight = 21.9824f
        ) {
            addSfPath("M3.80859 21.9824L18.5938 21.9824C22.7246 21.9824 24.1113 19.9902 24.6191 15.6543C24.9414 12.8906 24.9414 10.166 24.502 6.43555C23.9844 1.81641 21.5137-0.0195312 16.582 0.517578L4.375 1.89453C1.50391 2.2168 0 3.81836 0 6.49414L0 18.2031C0 20.5859 1.39648 21.9824 3.80859 21.9824Z", fillAlpha = 0.2125f)
            addSfPath("M5.55664 8.35938C5.14648 8.35938 4.81445 8.02734 4.81445 7.61719C4.81445 7.20703 5.14648 6.875 5.55664 6.875L19.1406 6.875C19.5605 6.875 19.8926 7.20703 19.8926 7.61719C19.8926 8.02734 19.5605 8.35938 19.1406 8.35938ZM5.55664 12.6367C5.14648 12.6367 4.81445 12.3047 4.81445 11.9043C4.81445 11.4941 5.14648 11.1621 5.55664 11.1621L19.1406 11.1621C19.5605 11.1621 19.8926 11.4941 19.8926 11.9043C19.8926 12.3047 19.5605 12.6367 19.1406 12.6367ZM5.55664 16.9238C5.14648 16.9238 4.81445 16.5918 4.81445 16.1816C4.81445 15.7812 5.14648 15.4395 5.55664 15.4395L19.1406 15.4395C19.5605 15.4395 19.8926 15.7812 19.8926 16.1816C19.8926 16.5918 19.5605 16.9238 19.1406 16.9238Z", fillAlpha = 0.85f)
        }
        return _sFPedalBrakeFill!!
    }

private var _sFPedalBrakeFill: ImageVector? = null
