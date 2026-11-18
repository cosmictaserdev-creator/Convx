package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFKeyHorizontalFill (monochrome)
 * Viewport: 31.1664 x 15.8887
 */
public val SfSymbols.Monochrome.SFKeyHorizontalFill: ImageVector
    get() {
        if (_sFKeyHorizontalFill != null) {
            return _sFKeyHorizontalFill!!
        }
        _sFKeyHorizontalFill = sfIcon(
            name = "Monochrome.SFKeyHorizontalFill",
            viewportWidth = 31.1664f,
            viewportHeight = 15.8887f
        ) {
            addSfPath("M30.7834 7.93945C30.7834 3.52539 27.258 0 22.8635 0C19.5237 0 16.5842 2.00195 15.4221 5.12695L3.01974 5.12695C2.72678 5.12695 2.4924 5.22461 2.25803 5.44922L0.177948 7.51953C-0.0271301 7.72461-0.0857238 8.10547 0.168182 8.35938L4.12326 12.3145C4.37717 12.5586 4.7092 12.5488 4.94357 12.3145L7.41428 9.82422L10.8225 13.2422C11.0471 13.457 11.3791 13.457 11.633 13.2031L15.0608 9.80469C16.5842 13.7012 19.4358 15.8789 22.8635 15.8789C27.2385 15.8789 30.7834 12.334 30.7834 7.93945ZM23.5373 7.93945C23.5373 6.79688 24.4553 5.88867 25.5881 5.88867C26.7307 5.88867 27.6389 6.78711 27.6389 7.93945C27.6389 9.07227 26.7307 9.99023 25.5881 9.99023C24.4553 9.99023 23.5373 9.07227 23.5373 7.93945Z", fillAlpha = 0.85f)
        }
        return _sFKeyHorizontalFill!!
    }

private var _sFKeyHorizontalFill: ImageVector? = null
