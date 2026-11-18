package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMinusRectanglePortraitFill (monochrome)
 * Viewport: 21.3281 x 26.9238
 */
public val SfSymbols.Monochrome.SFMinusRectanglePortraitFill: ImageVector
    get() {
        if (_sFMinusRectanglePortraitFill != null) {
            return _sFMinusRectanglePortraitFill!!
        }
        _sFMinusRectanglePortraitFill = sfIcon(
            name = "Monochrome.SFMinusRectanglePortraitFill",
            viewportWidth = 21.3281f,
            viewportHeight = 26.9238f
        ) {
            addSfPath("M20.9668 3.80859L20.9668 23.125C20.9668 25.6445 19.707 26.9238 17.207 26.9238L3.75977 26.9238C1.25977 26.9238 0 25.6445 0 23.125L0 3.80859C0 1.28906 1.25977 0.00976562 3.75977 0.00976562L17.207 0.00976562C19.707 0.00976562 20.9668 1.28906 20.9668 3.80859ZM5.21484 12.5488C4.58008 12.5488 4.16992 12.9004 4.16992 13.4961C4.16992 14.0723 4.59961 14.4141 5.21484 14.4141L15.7715 14.4141C16.3867 14.4141 16.8164 14.0723 16.8164 13.4961C16.8164 12.9004 16.416 12.5488 15.7715 12.5488Z", fillAlpha = 0.85f)
        }
        return _sFMinusRectanglePortraitFill!!
    }

private var _sFMinusRectanglePortraitFill: ImageVector? = null
