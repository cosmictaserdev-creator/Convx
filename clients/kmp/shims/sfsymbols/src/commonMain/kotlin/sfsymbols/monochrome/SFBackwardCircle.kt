package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBackwardCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFBackwardCircle: ImageVector
    get() {
        if (_sFBackwardCircle != null) {
            return _sFBackwardCircle!!
        }
        _sFBackwardCircle = sfIcon(
            name = "Monochrome.SFBackwardCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M11.0254 17.373C11.4453 17.373 11.8164 17.0508 11.8164 16.4258L11.8164 9.04297C11.8164 8.41797 11.4453 8.0957 11.0254 8.0957C10.791 8.0957 10.6055 8.17383 10.3809 8.31055L4.20898 11.9336C3.84766 12.1484 3.68164 12.4219 3.68164 12.7344C3.68164 13.0469 3.84766 13.3203 4.20898 13.5352L10.3809 17.1582C10.6055 17.2949 10.791 17.373 11.0254 17.373ZM19.1309 17.373C19.5605 17.373 19.9219 17.0508 19.9219 16.4258L19.9219 9.04297C19.9219 8.41797 19.5605 8.0957 19.1309 8.0957C18.9062 8.0957 18.7012 8.17383 18.4863 8.31055L12.3242 11.9336C11.9531 12.1484 11.7871 12.4219 11.7871 12.7344C11.7871 13.0469 11.9531 13.3203 12.3242 13.5352L18.4863 17.1582C18.7012 17.2949 18.9062 17.373 19.1309 17.373Z", fillAlpha = 0.85f)
        }
        return _sFBackwardCircle!!
    }

private var _sFBackwardCircle: ImageVector? = null
