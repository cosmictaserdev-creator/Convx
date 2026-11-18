package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCubeCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFCubeCircle: ImageVector
    get() {
        if (_sFCubeCircle != null) {
            return _sFCubeCircle!!
        }
        _sFCubeCircle = sfIcon(
            name = "Monochrome.SFCubeCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M12.7148 12.3926L19.0234 8.83789C18.916 8.73047 18.7891 8.61328 18.6426 8.54492L13.4277 5.52734C12.9785 5.25391 12.4609 5.25391 12.0117 5.52734L6.79688 8.54492C6.64062 8.61328 6.52344 8.73047 6.42578 8.84766ZM12.2266 13.2324L6.09375 9.78516C6.07422 9.80469 6.07422 9.84375 6.07422 9.88281L6.07422 15.8008C6.07422 16.3086 6.34766 16.7578 6.79688 17.0117L12.0117 20.0391C12.0898 20.0781 12.1484 20.1172 12.2266 20.1465ZM13.2031 13.2324L13.2031 20.1465C13.2715 20.1172 13.3496 20.0879 13.4277 20.0391L18.6426 17.0117C19.0918 16.7578 19.3555 16.3086 19.3555 15.8008L19.3555 9.87305C19.3555 9.84375 19.3555 9.80469 19.3555 9.77539Z", fillAlpha = 0.85f)
        }
        return _sFCubeCircle!!
    }

private var _sFCubeCircle: ImageVector? = null
