package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFAppBadgeFill (monochrome)
 * Viewport: 30.1367 x 30.5957
 */
public val SfSymbols.Monochrome.SFAppBadgeFill: ImageVector
    get() {
        if (_sFAppBadgeFill != null) {
            return _sFAppBadgeFill!!
        }
        _sFAppBadgeFill = sfIcon(
            name = "Monochrome.SFAppBadgeFill",
            viewportWidth = 30.1367f,
            viewportHeight = 30.5957f
        ) {
            addSfPath("M18.5449 5.79102C18.5449 8.97461 21.1621 11.5918 24.3457 11.5918C25.0451 11.5918 25.7175 11.4647 26.3379 11.2261L26.3379 19.9316C26.3379 22.1387 25.7324 23.8574 24.5801 25C23.457 26.123 21.7383 26.748 19.5215 26.748L10.2441 26.748C8.02734 26.748 6.31836 26.1328 5.18555 25C4.02344 23.8477 3.42773 22.1387 3.42773 19.9316L3.42773 10.6641C3.42773 8.45703 4.0332 6.73828 5.18555 5.5957C6.30859 4.47266 8.02734 3.84766 10.2441 3.84766L18.8926 3.84766C18.6655 4.45429 18.5449 5.10983 18.5449 5.79102Z", fillAlpha = 0.85f)
            addSfPath("M24.3457 10.0391C26.6602 10.0391 28.584 8.13477 28.584 5.80078C28.584 3.47656 26.6602 1.5625 24.3457 1.5625C22.0215 1.5625 20.1074 3.47656 20.1074 5.80078C20.1074 8.13477 22.0215 10.0391 24.3457 10.0391Z", fillAlpha = 0.85f)
        }
        return _sFAppBadgeFill!!
    }

private var _sFAppBadgeFill: ImageVector? = null
