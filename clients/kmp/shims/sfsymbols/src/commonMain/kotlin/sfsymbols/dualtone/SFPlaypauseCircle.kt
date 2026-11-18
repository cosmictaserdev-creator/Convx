package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPlaypauseCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFPlaypauseCircle: ImageVector
    get() {
        if (_sFPlaypauseCircle != null) {
            return _sFPlaypauseCircle!!
        }
        _sFPlaypauseCircle = sfIcon(
            name = "Dualtone.SFPlaypauseCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M5.74219 17.5098L12.2363 13.3594C12.7148 13.0469 12.7148 12.4316 12.2363 12.1191L5.74219 7.95898C5.25391 7.64648 4.59961 7.91016 4.59961 8.45703L4.59961 17.0215C4.59961 17.5684 5.22461 17.8418 5.74219 17.5098ZM14.4238 17.4512L15.625 17.4512C16.2109 17.4512 16.4746 17.1387 16.4746 16.6797L16.4746 8.7793C16.4746 8.31055 16.2109 8.00781 15.625 8.00781L14.4238 8.00781C13.8477 8.00781 13.584 8.31055 13.584 8.7793L13.584 16.6797C13.584 17.1387 13.8477 17.4512 14.4238 17.4512ZM18.8184 17.4512L20.0293 17.4512C20.5859 17.4512 20.8594 17.1387 20.8594 16.6797L20.8594 8.7793C20.8594 8.31055 20.5859 8.00781 20.0293 8.00781L18.8184 8.00781C18.252 8.00781 17.9785 8.31055 17.9785 8.7793L17.9785 16.6797C17.9785 17.1387 18.252 17.4512 18.8184 17.4512Z", fillAlpha = 0.85f)
        }
        return _sFPlaypauseCircle!!
    }

private var _sFPlaypauseCircle: ImageVector? = null
