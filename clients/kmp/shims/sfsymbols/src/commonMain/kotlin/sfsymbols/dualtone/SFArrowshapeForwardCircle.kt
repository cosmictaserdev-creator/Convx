package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowshapeForwardCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFArrowshapeForwardCircle: ImageVector
    get() {
        if (_sFArrowshapeForwardCircle != null) {
            return _sFArrowshapeForwardCircle!!
        }
        _sFArrowshapeForwardCircle = sfIcon(
            name = "Dualtone.SFArrowshapeForwardCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M6.94336 10.0684C5.9668 10.0684 5.41016 10.6055 5.41016 11.5527L5.41016 13.916C5.41016 14.8633 5.9668 15.4004 6.94336 15.4004L15.3125 15.4004C16.2891 15.4004 16.8457 14.8633 16.8457 13.916L16.8457 11.5527C16.8457 10.6055 16.2891 10.0684 15.3125 10.0684ZM12.8223 7.17773L12.8223 18.3008C12.8223 18.7305 13.125 19.0332 13.5547 19.0332C13.8574 19.0332 14.1113 18.8965 14.3945 18.6426L19.9023 13.457C20.1855 13.1836 20.2637 12.9297 20.2637 12.7246C20.2637 12.5 20.1855 12.2559 19.9023 11.9727L14.3945 6.8457C14.082 6.54297 13.8672 6.41602 13.5742 6.41602C13.125 6.41602 12.8223 6.75781 12.8223 7.17773Z", fillAlpha = 0.85f)
        }
        return _sFArrowshapeForwardCircle!!
    }

private var _sFArrowshapeForwardCircle: ImageVector? = null
