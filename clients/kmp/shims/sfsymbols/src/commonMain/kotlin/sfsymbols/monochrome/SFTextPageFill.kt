package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTextPageFill (monochrome)
 * Viewport: 21.3281 x 26.9238
 */
public val SfSymbols.Monochrome.SFTextPageFill: ImageVector
    get() {
        if (_sFTextPageFill != null) {
            return _sFTextPageFill!!
        }
        _sFTextPageFill = sfIcon(
            name = "Monochrome.SFTextPageFill",
            viewportWidth = 21.3281f,
            viewportHeight = 26.9238f
        ) {
            addSfPath("M20.9668 3.80859L20.9668 23.125C20.9668 25.6445 19.707 26.9238 17.207 26.9238L3.75977 26.9238C1.25977 26.9238 0 25.6445 0 23.125L0 3.80859C0 1.28906 1.25977 0.00976562 3.75977 0.00976562L17.207 0.00976562C19.707 0.00976562 20.9668 1.28906 20.9668 3.80859ZM5.48828 14.4629C5.07812 14.4629 4.77539 14.7754 4.77539 15.166C4.77539 15.5566 5.07812 15.8594 5.48828 15.8594L10.2734 15.8594C10.6836 15.8594 10.9766 15.5566 10.9766 15.166C10.9766 14.7754 10.6836 14.4629 10.2734 14.4629ZM5.48828 9.94141C5.07812 9.94141 4.77539 10.2441 4.77539 10.625C4.77539 11.0254 5.07812 11.3379 5.48828 11.3379L15.4883 11.3379C15.8887 11.3379 16.1914 11.0254 16.1914 10.625C16.1914 10.2441 15.8887 9.94141 15.4883 9.94141ZM5.48828 5.41992C5.07812 5.41992 4.77539 5.72266 4.77539 6.10352C4.77539 6.50391 5.07812 6.81641 5.48828 6.81641L15.4883 6.81641C15.8887 6.81641 16.1914 6.50391 16.1914 6.10352C16.1914 5.72266 15.8887 5.41992 15.4883 5.41992Z", fillAlpha = 0.85f)
        }
        return _sFTextPageFill!!
    }

private var _sFTextPageFill: ImageVector? = null
