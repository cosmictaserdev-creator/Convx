package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTextPageFill (dualtone)
 * Viewport: 21.3281 x 26.9238
 */
public val SfSymbols.Dualtone.SFTextPageFill: ImageVector
    get() {
        if (_sFTextPageFill != null) {
            return _sFTextPageFill!!
        }
        _sFTextPageFill = sfIcon(
            name = "Dualtone.SFTextPageFill",
            viewportWidth = 21.3281f,
            viewportHeight = 26.9238f
        ) {
            addSfPath("M0 23.125C0 25.6445 1.25977 26.9238 3.75977 26.9238L17.207 26.9238C19.707 26.9238 20.9668 25.6445 20.9668 23.125L20.9668 3.80859C20.9668 1.28906 19.707 0.00976562 17.207 0.00976562L3.75977 0.00976562C1.25977 0.00976562 0 1.28906 0 3.80859Z", fillAlpha = 0.2125f)
            addSfPath("M5.48828 6.81641C5.07812 6.81641 4.77539 6.50391 4.77539 6.10352C4.77539 5.72266 5.07812 5.41992 5.48828 5.41992L15.4883 5.41992C15.8887 5.41992 16.1914 5.72266 16.1914 6.10352C16.1914 6.50391 15.8887 6.81641 15.4883 6.81641Z", fillAlpha = 0.85f)
            addSfPath("M5.48828 11.3379C5.07812 11.3379 4.77539 11.0254 4.77539 10.625C4.77539 10.2441 5.07812 9.94141 5.48828 9.94141L15.4883 9.94141C15.8887 9.94141 16.1914 10.2441 16.1914 10.625C16.1914 11.0254 15.8887 11.3379 15.4883 11.3379Z", fillAlpha = 0.85f)
            addSfPath("M5.48828 15.8594C5.07812 15.8594 4.77539 15.5566 4.77539 15.166C4.77539 14.7754 5.07812 14.4629 5.48828 14.4629L10.2734 14.4629C10.6836 14.4629 10.9766 14.7754 10.9766 15.166C10.9766 15.5566 10.6836 15.8594 10.2734 15.8594Z", fillAlpha = 0.85f)
        }
        return _sFTextPageFill!!
    }

private var _sFTextPageFill: ImageVector? = null
