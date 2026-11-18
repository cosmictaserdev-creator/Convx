package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMinusRectanglePortrait (monochrome)
 * Viewport: 21.3281 x 26.9238
 */
public val SfSymbols.Monochrome.SFMinusRectanglePortrait: ImageVector
    get() {
        if (_sFMinusRectanglePortrait != null) {
            return _sFMinusRectanglePortrait!!
        }
        _sFMinusRectanglePortrait = sfIcon(
            name = "Monochrome.SFMinusRectanglePortrait",
            viewportWidth = 21.3281f,
            viewportHeight = 26.9238f
        ) {
            addSfPath("M0 23.125C0 25.6445 1.25977 26.9238 3.75977 26.9238L17.207 26.9238C19.707 26.9238 20.9668 25.6445 20.9668 23.125L20.9668 3.80859C20.9668 1.28906 19.707 0.00976562 17.207 0.00976562L3.75977 0.00976562C1.25977 0.00976562 0 1.28906 0 3.80859ZM1.72852 23.0859L1.72852 3.84766C1.72852 2.48047 2.45117 1.73828 3.85742 1.73828L17.1094 1.73828C18.5156 1.73828 19.2383 2.48047 19.2383 3.84766L19.2383 23.0859C19.2383 24.4531 18.5156 25.1953 17.1094 25.1953L3.85742 25.1953C2.45117 25.1953 1.72852 24.4531 1.72852 23.0859Z", fillAlpha = 0.85f)
            addSfPath("M5.33203 14.375L15.6543 14.375C16.25 14.375 16.6699 14.043 16.6699 13.4863C16.6699 12.9102 16.2793 12.5684 15.6543 12.5684L5.33203 12.5684C4.7168 12.5684 4.31641 12.9102 4.31641 13.4863C4.31641 14.043 4.74609 14.375 5.33203 14.375Z", fillAlpha = 0.85f)
        }
        return _sFMinusRectanglePortrait!!
    }

private var _sFMinusRectanglePortrait: ImageVector? = null
