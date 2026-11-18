package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMinusRectanglePortraitFill (dualtone)
 * Viewport: 21.3281 x 26.9238
 */
public val SfSymbols.Dualtone.SFMinusRectanglePortraitFill: ImageVector
    get() {
        if (_sFMinusRectanglePortraitFill != null) {
            return _sFMinusRectanglePortraitFill!!
        }
        _sFMinusRectanglePortraitFill = sfIcon(
            name = "Dualtone.SFMinusRectanglePortraitFill",
            viewportWidth = 21.3281f,
            viewportHeight = 26.9238f
        ) {
            addSfPath("M0 23.125C0 25.6445 1.25977 26.9238 3.75977 26.9238L17.207 26.9238C19.707 26.9238 20.9668 25.6445 20.9668 23.125L20.9668 3.80859C20.9668 1.28906 19.707 0.00976562 17.207 0.00976562L3.75977 0.00976562C1.25977 0.00976562 0 1.28906 0 3.80859Z", fillAlpha = 0.2125f)
            addSfPath("M5.21484 14.4141C4.59961 14.4141 4.16992 14.0723 4.16992 13.4961C4.16992 12.9004 4.58008 12.5488 5.21484 12.5488L15.7715 12.5488C16.416 12.5488 16.8164 12.9004 16.8164 13.4961C16.8164 14.0723 16.3867 14.4141 15.7715 14.4141Z", fillAlpha = 0.85f)
        }
        return _sFMinusRectanglePortraitFill!!
    }

private var _sFMinusRectanglePortraitFill: ImageVector? = null
