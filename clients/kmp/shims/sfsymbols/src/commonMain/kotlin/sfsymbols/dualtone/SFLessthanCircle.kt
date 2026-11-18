package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLessthanCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFLessthanCircle: ImageVector
    get() {
        if (_sFLessthanCircle != null) {
            return _sFLessthanCircle!!
        }
        _sFLessthanCircle = sfIcon(
            name = "Dualtone.SFLessthanCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M15.8984 17.7051C16.416 17.7051 16.7969 17.3438 16.7969 16.8457C16.7969 16.4355 16.6211 16.1914 16.1719 15.9766L9.7168 12.7344L9.7168 12.6562L16.1719 9.36523C16.6113 9.14062 16.7969 8.87695 16.7969 8.47656C16.7969 7.99805 16.4258 7.62695 15.918 7.62695C15.6836 7.62695 15.5469 7.66602 15.3711 7.75391L7.97852 11.7676C7.54883 12.0117 7.34375 12.3145 7.34375 12.7246C7.34375 13.1738 7.5293 13.4473 7.97852 13.6816L15.3711 17.5781C15.5371 17.666 15.6836 17.7051 15.8984 17.7051Z", fillAlpha = 0.85f)
        }
        return _sFLessthanCircle!!
    }

private var _sFLessthanCircle: ImageVector? = null
