package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFInsetFilledTophalfRectanglePortrait (dualtone)
 * Viewport: 21.3281 x 26.9238
 */
public val SfSymbols.Dualtone.SFInsetFilledTophalfRectanglePortrait: ImageVector
    get() {
        if (_sFInsetFilledTophalfRectanglePortrait != null) {
            return _sFInsetFilledTophalfRectanglePortrait!!
        }
        _sFInsetFilledTophalfRectanglePortrait = sfIcon(
            name = "Dualtone.SFInsetFilledTophalfRectanglePortrait",
            viewportWidth = 21.3281f,
            viewportHeight = 26.9238f
        ) {
            addSfPath("M0 23.125C0 25.6445 1.25977 26.9238 3.75977 26.9238L17.207 26.9238C19.707 26.9238 20.9668 25.6445 20.9668 23.125L20.9668 3.80859C20.9668 1.28906 19.707 0.00976562 17.207 0.00976562L3.75977 0.00976562C1.25977 0.00976562 0 1.28906 0 3.80859ZM1.72852 23.0859L1.72852 3.84766C1.72852 2.48047 2.45117 1.73828 3.85742 1.73828L17.1094 1.73828C18.5156 1.73828 19.2383 2.48047 19.2383 3.84766L19.2383 23.0859C19.2383 24.4531 18.5156 25.1953 17.1094 25.1953L3.85742 25.1953C2.45117 25.1953 1.72852 24.4531 1.72852 23.0859Z", fillAlpha = 0.425f)
            addSfPath("M4.53125 13.457L16.4355 13.457C17.2949 13.457 17.6758 13.0859 17.6758 12.2266L17.6758 4.52148C17.6758 3.67188 17.2949 3.30078 16.4355 3.30078L4.53125 3.30078C3.67188 3.30078 3.29102 3.67188 3.29102 4.52148L3.29102 12.2266C3.29102 13.0859 3.67188 13.457 4.53125 13.457Z", fillAlpha = 0.85f)
        }
        return _sFInsetFilledTophalfRectanglePortrait!!
    }

private var _sFInsetFilledTophalfRectanglePortrait: ImageVector? = null
