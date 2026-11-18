package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFChevronUpCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFChevronUpCircle: ImageVector
    get() {
        if (_sFChevronUpCircle != null) {
            return _sFChevronUpCircle!!
        }
        _sFChevronUpCircle = sfIcon(
            name = "Monochrome.SFChevronUpCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M6.51367 15.6055C6.82617 15.9473 7.36328 15.9473 7.66602 15.6152L12.7148 10.3223L17.7637 15.6152C18.0762 15.9473 18.6035 15.9473 18.9258 15.6055C19.2285 15.3027 19.2285 14.8047 18.8965 14.4629L13.7793 9.08203C13.1055 8.36914 12.3242 8.36914 11.6602 9.08203L6.54297 14.4629C6.21094 14.8047 6.21094 15.3027 6.51367 15.6055Z", fillAlpha = 0.85f)
        }
        return _sFChevronUpCircle!!
    }

private var _sFChevronUpCircle: ImageVector? = null
