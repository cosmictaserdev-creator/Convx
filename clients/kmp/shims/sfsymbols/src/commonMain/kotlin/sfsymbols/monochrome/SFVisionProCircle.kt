package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFVisionProCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFVisionProCircle: ImageVector
    get() {
        if (_sFVisionProCircle != null) {
            return _sFVisionProCircle!!
        }
        _sFVisionProCircle = sfIcon(
            name = "Monochrome.SFVisionProCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M12.7051 15.5371C13.7793 15.5371 14.9609 17.4707 17.3047 17.4707C19.5996 17.4707 21.2988 15.3809 21.2988 12.6855C21.2988 8.24219 17.4023 7.75391 12.7051 7.75391C8.00781 7.75391 4.11133 8.24219 4.11133 12.6855C4.11133 15.3809 5.82031 17.4707 8.11523 17.4707C10.4492 17.4707 11.6309 15.5371 12.7051 15.5371ZM12.7051 14.3262C10.8789 14.3262 10.0293 16.25 8.11523 16.25C6.39648 16.25 5.32227 14.9316 5.32227 12.6855C5.32227 9.31641 8.14453 8.97461 12.7051 8.97461C17.2754 8.97461 20.0977 9.31641 20.0977 12.6855C20.0977 14.9316 19.0137 16.25 17.3047 16.25C15.3906 16.25 14.5312 14.3262 12.7051 14.3262Z", fillAlpha = 0.85f)
        }
        return _sFVisionProCircle!!
    }

private var _sFVisionProCircle: ImageVector? = null
