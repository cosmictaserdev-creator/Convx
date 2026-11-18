package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SF1Circle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SF1Circle: ImageVector
    get() {
        if (_sF1Circle != null) {
            return _sF1Circle!!
        }
        _sF1Circle = sfIcon(
            name = "Monochrome.SF1Circle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M13.3203 18.6621C13.877 18.6621 14.1992 18.3008 14.1992 17.6953L14.1992 7.75391C14.1992 7.13867 13.8477 6.78711 13.2715 6.78711C12.8711 6.78711 12.5879 6.89453 12.1191 7.2168L9.75586 8.79883C9.51172 8.96484 9.41406 9.14062 9.41406 9.42383C9.41406 9.76562 9.6875 10.0781 10.0391 10.0781C10.2051 10.0781 10.3223 10.0488 10.5566 9.89258L12.3926 8.69141L12.4707 8.69141L12.4707 17.6953C12.4707 18.3008 12.793 18.6621 13.3203 18.6621Z", fillAlpha = 0.85f)
        }
        return _sF1Circle!!
    }

private var _sF1Circle: ImageVector? = null
