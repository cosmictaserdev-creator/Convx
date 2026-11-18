package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCheckmarkRectanglePortraitFill (monochrome)
 * Viewport: 21.3281 x 26.9238
 */
public val SfSymbols.Monochrome.SFCheckmarkRectanglePortraitFill: ImageVector
    get() {
        if (_sFCheckmarkRectanglePortraitFill != null) {
            return _sFCheckmarkRectanglePortraitFill!!
        }
        _sFCheckmarkRectanglePortraitFill = sfIcon(
            name = "Monochrome.SFCheckmarkRectanglePortraitFill",
            viewportWidth = 21.3281f,
            viewportHeight = 26.9238f
        ) {
            addSfPath("M20.9668 3.80859L20.9668 23.125C20.9668 25.6445 19.707 26.9238 17.207 26.9238L3.75977 26.9238C1.25977 26.9238 0 25.6445 0 23.125L0 3.80859C0 1.28906 1.25977 0.00976562 3.75977 0.00976562L17.207 0.00976562C19.707 0.00976562 20.9668 1.28906 20.9668 3.80859ZM14.6973 8.44727L9.05273 17.4219L6.24023 13.9062C5.97656 13.5645 5.73242 13.457 5.42969 13.457C4.94141 13.457 4.56055 13.8574 4.56055 14.3457C4.56055 14.5898 4.6582 14.834 4.82422 15.0488L8.1543 19.0723C8.4375 19.4434 8.7207 19.5898 9.0918 19.5898C9.46289 19.5898 9.76562 19.4141 9.99023 19.0723L16.1133 9.48242C16.2402 9.26758 16.3672 9.01367 16.3672 8.7793C16.3672 8.28125 15.9375 7.94922 15.4688 7.94922C15.1758 7.94922 14.8926 8.125 14.6973 8.44727Z", fillAlpha = 0.85f)
        }
        return _sFCheckmarkRectanglePortraitFill!!
    }

private var _sFCheckmarkRectanglePortraitFill: ImageVector? = null
