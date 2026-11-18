package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRectanglePortraitSplit2x1Fill (monochrome)
 * Viewport: 21.3281 x 26.9238
 */
public val SfSymbols.Monochrome.SFRectanglePortraitSplit2x1Fill: ImageVector
    get() {
        if (_sFRectanglePortraitSplit2x1Fill != null) {
            return _sFRectanglePortraitSplit2x1Fill!!
        }
        _sFRectanglePortraitSplit2x1Fill = sfIcon(
            name = "Monochrome.SFRectanglePortraitSplit2x1Fill",
            viewportWidth = 21.3281f,
            viewportHeight = 26.9238f
        ) {
            addSfPath("M11.3477 26.9238L9.61914 26.9238L9.61914 0.00976562L11.3477 0.00976562ZM0 23.125C0 25.6445 1.25977 26.9238 3.75977 26.9238L17.207 26.9238C19.707 26.9238 20.9668 25.6445 20.9668 23.125L20.9668 3.80859C20.9668 1.28906 19.707 0.00976562 17.207 0.00976562L3.75977 0.00976562C1.25977 0.00976562 0 1.28906 0 3.80859Z", fillAlpha = 0.85f)
        }
        return _sFRectanglePortraitSplit2x1Fill!!
    }

private var _sFRectanglePortraitSplit2x1Fill: ImageVector? = null
