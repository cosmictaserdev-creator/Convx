package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRestartCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFRestartCircle: ImageVector
    get() {
        if (_sFRestartCircle != null) {
            return _sFRestartCircle!!
        }
        _sFRestartCircle = sfIcon(
            name = "Monochrome.SFRestartCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M7.04102 11.5234C6.09375 12.0898 6.11328 13.3789 7.02148 13.9258L14.502 18.3789C15.3711 18.8965 16.5625 18.5254 16.5625 17.3242L16.5625 8.0957C16.5625 6.88477 15.3711 6.52344 14.502 7.04102ZM8.63281 12.5781L14.5605 9.0332C14.7266 8.93555 14.834 9.00391 14.834 9.12109L14.834 16.2891C14.834 16.416 14.7266 16.4844 14.5605 16.3867L8.64258 12.8711C8.50586 12.793 8.52539 12.6562 8.63281 12.5781Z", fillAlpha = 0.85f)
        }
        return _sFRestartCircle!!
    }

private var _sFRestartCircle: ImageVector? = null
