package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCheckmarkRectanglePortraitFill (dualtone)
 * Viewport: 21.3281 x 26.9238
 */
public val SfSymbols.Dualtone.SFCheckmarkRectanglePortraitFill: ImageVector
    get() {
        if (_sFCheckmarkRectanglePortraitFill != null) {
            return _sFCheckmarkRectanglePortraitFill!!
        }
        _sFCheckmarkRectanglePortraitFill = sfIcon(
            name = "Dualtone.SFCheckmarkRectanglePortraitFill",
            viewportWidth = 21.3281f,
            viewportHeight = 26.9238f
        ) {
            addSfPath("M0 23.125C0 25.6445 1.25977 26.9238 3.75977 26.9238L17.207 26.9238C19.707 26.9238 20.9668 25.6445 20.9668 23.125L20.9668 3.80859C20.9668 1.28906 19.707 0.00976562 17.207 0.00976562L3.75977 0.00976562C1.25977 0.00976562 0 1.28906 0 3.80859Z", fillAlpha = 0.2125f)
            addSfPath("M9.0918 19.5898C8.7207 19.5898 8.4375 19.4434 8.1543 19.0723L4.82422 15.0488C4.6582 14.834 4.56055 14.5898 4.56055 14.3457C4.56055 13.8574 4.94141 13.457 5.42969 13.457C5.73242 13.457 5.97656 13.5645 6.24023 13.9062L9.05273 17.4219L14.6973 8.44727C14.8926 8.125 15.1758 7.94922 15.4688 7.94922C15.9375 7.94922 16.3672 8.28125 16.3672 8.7793C16.3672 9.01367 16.2402 9.26758 16.1133 9.48242L9.99023 19.0723C9.76562 19.4141 9.46289 19.5898 9.0918 19.5898Z", fillAlpha = 0.85f)
        }
        return _sFCheckmarkRectanglePortraitFill!!
    }

private var _sFCheckmarkRectanglePortraitFill: ImageVector? = null
