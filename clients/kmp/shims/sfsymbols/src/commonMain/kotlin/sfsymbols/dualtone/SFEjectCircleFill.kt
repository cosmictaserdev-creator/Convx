package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFEjectCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFEjectCircleFill: ImageVector
    get() {
        if (_sFEjectCircleFill != null) {
            return _sFEjectCircleFill!!
        }
        _sFEjectCircleFill = sfIcon(
            name = "Dualtone.SFEjectCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M7.56836 18.0469C6.82617 18.0469 6.39648 17.6367 6.39648 16.8945L6.39648 16.084C6.39648 15.3418 6.82617 14.9316 7.56836 14.9316L17.8613 14.9316C18.6035 14.9316 19.0332 15.3418 19.0332 16.084L19.0332 16.8945C19.0332 17.6367 18.6035 18.0469 17.8613 18.0469ZM7.28516 13.2617C6.78711 13.2617 6.54297 12.998 6.51367 12.5684C6.50391 12.3438 6.5918 12.1387 6.78711 11.9434L11.9629 6.17188C12.1484 5.95703 12.4121 5.79102 12.7148 5.79102C13.0176 5.79102 13.2812 5.92773 13.4766 6.17188L18.6328 11.9434C18.8184 12.1484 18.9258 12.3438 18.9258 12.5684C18.9258 12.998 18.623 13.2617 18.1348 13.2617Z", fillAlpha = 0.85f)
        }
        return _sFEjectCircleFill!!
    }

private var _sFEjectCircleFill: ImageVector? = null
