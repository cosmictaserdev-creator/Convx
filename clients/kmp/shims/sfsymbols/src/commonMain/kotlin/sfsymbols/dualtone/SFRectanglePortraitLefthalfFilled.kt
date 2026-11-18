package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRectanglePortraitLefthalfFilled (dualtone)
 * Viewport: 21.3281 x 26.9238
 */
public val SfSymbols.Dualtone.SFRectanglePortraitLefthalfFilled: ImageVector
    get() {
        if (_sFRectanglePortraitLefthalfFilled != null) {
            return _sFRectanglePortraitLefthalfFilled!!
        }
        _sFRectanglePortraitLefthalfFilled = sfIcon(
            name = "Dualtone.SFRectanglePortraitLefthalfFilled",
            viewportWidth = 21.3281f,
            viewportHeight = 26.9238f
        ) {
            addSfPath("M0 23.125C0 25.6445 1.25977 26.9238 3.75977 26.9238L17.207 26.9238C19.707 26.9238 20.9668 25.6445 20.9668 23.125L20.9668 3.80859C20.9668 1.28906 19.707 0.00976562 17.207 0.00976562L3.75977 0.00976562C1.25977 0.00976562 0 1.28906 0 3.80859ZM10.4883 25.1953L10.4883 1.73828L17.1094 1.73828C18.5156 1.73828 19.2383 2.48047 19.2383 3.84766L19.2383 23.0859C19.2383 24.4531 18.5156 25.1953 17.1094 25.1953Z", fillAlpha = 0.85f)
        }
        return _sFRectanglePortraitLefthalfFilled!!
    }

private var _sFRectanglePortraitLefthalfFilled: ImageVector? = null
