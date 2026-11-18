package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCharacterDuployan (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFCharacterDuployan: ImageVector
    get() {
        if (_sFCharacterDuployan != null) {
            return _sFCharacterDuployan!!
        }
        _sFCharacterDuployan = sfIcon(
            name = "Monochrome.SFCharacterDuployan",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7051 12.4707C16.123 12.4707 18.9551 9.6582 18.9551 6.25C18.9551 2.83203 16.123 0.00976562 12.7051 0.00976562C9.27734 0.00976562 6.46484 2.83203 6.46484 6.25C6.46484 9.6582 9.29688 12.4707 12.7051 12.4707ZM12.7051 10.6641C10.2051 10.6641 8.28125 8.74023 8.28125 6.25C8.28125 3.75 10.2051 1.82617 12.7051 1.82617C15.2051 1.82617 17.1484 3.75 17.1484 6.25C17.1484 8.74023 15.2051 10.6641 12.7051 10.6641ZM12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
        }
        return _sFCharacterDuployan!!
    }

private var _sFCharacterDuployan: ImageVector? = null
