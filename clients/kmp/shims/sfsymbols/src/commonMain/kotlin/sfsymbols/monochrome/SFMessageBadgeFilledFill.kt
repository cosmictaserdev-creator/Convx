package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMessageBadgeFilledFill (monochrome)
 * Viewport: 32.5879 x 29.9121
 */
public val SfSymbols.Monochrome.SFMessageBadgeFilledFill: ImageVector
    get() {
        if (_sFMessageBadgeFilledFill != null) {
            return _sFMessageBadgeFilledFill!!
        }
        _sFMessageBadgeFilledFill = sfIcon(
            name = "Monochrome.SFMessageBadgeFilledFill",
            viewportWidth = 32.5879f,
            viewportHeight = 29.9121f
        ) {
            addSfPath("M21.7891 2.89898C21.2838 3.75208 20.9961 4.7453 20.9961 5.80078C20.9961 8.97461 23.6133 11.5918 26.7969 11.5918C27.9546 11.5918 29.0383 11.2435 29.9459 10.644C30.2802 11.6735 30.4492 12.7775 30.4492 13.9355C30.4492 20.8203 24.4336 25.8398 16.1133 25.8398C13.3496 25.8398 10.8105 25.3027 8.66211 24.2871C7.39258 25.2344 5.61523 25.8398 3.92578 25.8398C3.20312 25.8398 2.95898 25.2637 3.44727 24.8242C4.19922 24.1504 4.54102 23.5059 4.54102 22.5098C4.54102 20.2441 1.78711 18.916 1.78711 13.9355C1.78711 7.02148 7.80273 2.03125 16.1133 2.03125C18.1637 2.03125 20.075 2.33501 21.7891 2.89898Z", fillAlpha = 0.85f)
            addSfPath("M26.7969 10.0488C29.1113 10.0488 31.0352 8.13477 31.0352 5.81055C31.0352 3.47656 29.1113 1.57227 26.7969 1.57227C24.4727 1.57227 22.5586 3.47656 22.5586 5.81055C22.5586 8.13477 24.4727 10.0488 26.7969 10.0488Z", fillAlpha = 0.85f)
        }
        return _sFMessageBadgeFilledFill!!
    }

private var _sFMessageBadgeFilledFill: ImageVector? = null
