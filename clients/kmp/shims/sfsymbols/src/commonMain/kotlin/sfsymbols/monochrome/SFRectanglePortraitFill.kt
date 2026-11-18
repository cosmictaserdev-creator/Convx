package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRectanglePortraitFill (monochrome)
 * Viewport: 21.3281 x 26.9238
 */
public val SfSymbols.Monochrome.SFRectanglePortraitFill: ImageVector
    get() {
        if (_sFRectanglePortraitFill != null) {
            return _sFRectanglePortraitFill!!
        }
        _sFRectanglePortraitFill = sfIcon(
            name = "Monochrome.SFRectanglePortraitFill",
            viewportWidth = 21.3281f,
            viewportHeight = 26.9238f
        ) {
            addSfPath("M0 23.125C0 25.6445 1.25977 26.9238 3.75977 26.9238L17.207 26.9238C19.707 26.9238 20.9668 25.6445 20.9668 23.125L20.9668 3.80859C20.9668 1.28906 19.707 0.00976562 17.207 0.00976562L3.75977 0.00976562C1.25977 0.00976562 0 1.28906 0 3.80859Z", fillAlpha = 0.85f)
        }
        return _sFRectanglePortraitFill!!
    }

private var _sFRectanglePortraitFill: ImageVector? = null
