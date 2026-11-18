package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFEjectCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFEjectCircleFill: ImageVector
    get() {
        if (_sFEjectCircleFill != null) {
            return _sFEjectCircleFill!!
        }
        _sFEjectCircleFill = sfIcon(
            name = "Monochrome.SFEjectCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM7.56836 14.9316C6.82617 14.9316 6.39648 15.3418 6.39648 16.084L6.39648 16.8945C6.39648 17.6367 6.82617 18.0469 7.56836 18.0469L17.8613 18.0469C18.6035 18.0469 19.0332 17.6367 19.0332 16.8945L19.0332 16.084C19.0332 15.3418 18.6035 14.9316 17.8613 14.9316ZM11.9629 6.17188L6.78711 11.9434C6.5918 12.1387 6.50391 12.3438 6.51367 12.5684C6.54297 12.998 6.78711 13.2617 7.28516 13.2617L18.1348 13.2617C18.623 13.2617 18.9258 12.998 18.9258 12.5684C18.9258 12.3438 18.8184 12.1484 18.6328 11.9434L13.4766 6.17188C13.2812 5.92773 13.0176 5.79102 12.7148 5.79102C12.4121 5.79102 12.1484 5.95703 11.9629 6.17188Z", fillAlpha = 0.85f)
        }
        return _sFEjectCircleFill!!
    }

private var _sFEjectCircleFill: ImageVector? = null
