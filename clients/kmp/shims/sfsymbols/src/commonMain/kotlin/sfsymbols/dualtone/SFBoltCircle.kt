package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBoltCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFBoltCircle: ImageVector
    get() {
        if (_sFBoltCircle != null) {
            return _sFBoltCircle!!
        }
        _sFBoltCircle = sfIcon(
            name = "Dualtone.SFBoltCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M7.14844 13.6328C7.14844 13.9258 7.38281 14.1309 7.67578 14.1309L12.0215 14.1309L9.69727 20.4004C9.4043 21.1523 10.2051 21.5527 10.6934 20.957L17.7246 12.1289C17.8516 11.9727 17.9199 11.8066 17.9199 11.6602C17.9199 11.3672 17.6855 11.1719 17.3926 11.1719L13.0469 11.1719L15.3711 4.90234C15.6641 4.15039 14.8633 3.74023 14.375 4.3457L7.34375 13.1641C7.2168 13.3301 7.14844 13.4961 7.14844 13.6328Z", fillAlpha = 0.85f)
        }
        return _sFBoltCircle!!
    }

private var _sFBoltCircle: ImageVector? = null
