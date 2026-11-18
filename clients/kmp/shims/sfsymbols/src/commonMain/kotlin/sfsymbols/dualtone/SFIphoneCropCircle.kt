package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFIphoneCropCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFIphoneCropCircle: ImageVector
    get() {
        if (_sFIphoneCropCircle != null) {
            return _sFIphoneCropCircle!!
        }
        _sFIphoneCropCircle = sfIcon(
            name = "Dualtone.SFIphoneCropCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M18.4375 22.7344L20.0195 22.7344L20.0195 9.61914C20.0195 7.77344 18.8184 6.61133 16.9141 6.61133L8.50586 6.61133C6.5918 6.61133 5.40039 7.77344 5.40039 9.61914L5.40039 22.7344L6.98242 22.7344L6.98242 9.85352C6.98242 8.75977 7.56836 8.19336 8.69141 8.19336L16.7188 8.19336C17.8516 8.19336 18.4375 8.75977 18.4375 9.85352ZM11.3184 10.459L14.1016 10.459C14.5215 10.459 14.8438 10.1367 14.8438 9.7168C14.8438 9.29688 14.5215 8.97461 14.1016 8.97461L11.3184 8.97461C10.8887 8.97461 10.5762 9.29688 10.5762 9.7168C10.5762 10.1367 10.8887 10.459 11.3184 10.459ZM12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
        }
        return _sFIphoneCropCircle!!
    }

private var _sFIphoneCropCircle: ImageVector? = null
