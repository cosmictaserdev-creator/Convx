package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFGreaterthanCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFGreaterthanCircle: ImageVector
    get() {
        if (_sFGreaterthanCircle != null) {
            return _sFGreaterthanCircle!!
        }
        _sFGreaterthanCircle = sfIcon(
            name = "Dualtone.SFGreaterthanCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M9.50195 17.7051C9.7168 17.7051 9.86328 17.666 10.0293 17.5781L17.4316 13.6816C17.8711 13.4473 18.0664 13.1738 18.0664 12.7246C18.0664 12.3145 17.8516 12.0117 17.4316 11.7676L10.0293 7.75391C9.85352 7.66602 9.7168 7.62695 9.48242 7.62695C8.97461 7.62695 8.60352 7.99805 8.60352 8.47656C8.60352 8.87695 8.79883 9.14062 9.23828 9.36523L15.6836 12.6562L15.6836 12.7344L9.23828 15.9766C8.78906 16.1914 8.60352 16.4355 8.60352 16.8457C8.60352 17.3438 8.99414 17.7051 9.50195 17.7051Z", fillAlpha = 0.85f)
        }
        return _sFGreaterthanCircle!!
    }

private var _sFGreaterthanCircle: ImageVector? = null
