package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRecordCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFRecordCircle: ImageVector
    get() {
        if (_sFRecordCircle != null) {
            return _sFRecordCircle!!
        }
        _sFRecordCircle = sfIcon(
            name = "Monochrome.SFRecordCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M12.7051 17.5879C15.4102 17.5879 17.5977 15.3906 17.5977 12.6953C17.5977 9.99023 15.4102 7.80273 12.7051 7.80273C10.0098 7.80273 7.8125 9.99023 7.8125 12.6953C7.8125 15.3906 10.0098 17.5879 12.7051 17.5879Z", fillAlpha = 0.85f)
        }
        return _sFRecordCircle!!
    }

private var _sFRecordCircle: ImageVector? = null
