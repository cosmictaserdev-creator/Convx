package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFInsetFilledCenterRectanglePortrait (monochrome)
 * Viewport: 21.3281 x 26.9238
 */
public val SfSymbols.Monochrome.SFInsetFilledCenterRectanglePortrait: ImageVector
    get() {
        if (_sFInsetFilledCenterRectanglePortrait != null) {
            return _sFInsetFilledCenterRectanglePortrait!!
        }
        _sFInsetFilledCenterRectanglePortrait = sfIcon(
            name = "Monochrome.SFInsetFilledCenterRectanglePortrait",
            viewportWidth = 21.3281f,
            viewportHeight = 26.9238f
        ) {
            addSfPath("M0 23.125C0 25.6445 1.25977 26.9238 3.75977 26.9238L17.207 26.9238C19.707 26.9238 20.9668 25.6445 20.9668 23.125L20.9668 3.80859C20.9668 1.28906 19.707 0.00976562 17.207 0.00976562L3.75977 0.00976562C1.25977 0.00976562 0 1.28906 0 3.80859ZM1.72852 23.0859L1.72852 3.84766C1.72852 2.48047 2.45117 1.73828 3.85742 1.73828L17.1094 1.73828C18.5156 1.73828 19.2383 2.48047 19.2383 3.84766L19.2383 23.0859C19.2383 24.4531 18.5156 25.1953 17.1094 25.1953L3.85742 25.1953C2.45117 25.1953 1.72852 24.4531 1.72852 23.0859Z", fillAlpha = 0.85f)
            addSfPath("M6.83594 21.2402L14.1309 21.2402C15.0781 21.2402 15.4785 20.8398 15.4785 19.9121L15.4785 7.02148C15.4785 6.09375 15.0781 5.69336 14.1309 5.69336L6.83594 5.69336C5.88867 5.69336 5.48828 6.09375 5.48828 7.02148L5.48828 19.9121C5.48828 20.8398 5.88867 21.2402 6.83594 21.2402Z", fillAlpha = 0.85f)
        }
        return _sFInsetFilledCenterRectanglePortrait!!
    }

private var _sFInsetFilledCenterRectanglePortrait: ImageVector? = null
