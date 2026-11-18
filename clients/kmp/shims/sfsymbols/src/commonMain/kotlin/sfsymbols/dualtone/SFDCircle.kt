package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFDCircle: ImageVector
    get() {
        if (_sFDCircle != null) {
            return _sFDCircle!!
        }
        _sFDCircle = sfIcon(
            name = "Dualtone.SFDCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M9.33594 18.3398L12.5879 18.3398C16.0645 18.3398 18.0664 16.2988 18.0664 12.5977C18.0664 8.98438 16.084 6.93359 12.5879 6.93359L9.33594 6.93359C8.78906 6.93359 8.49609 7.32422 8.49609 7.90039L8.49609 17.3633C8.49609 17.9395 8.78906 18.3398 9.33594 18.3398ZM10.1758 16.9727L10.1758 8.30078L12.3828 8.30078C14.8828 8.30078 16.3184 9.83398 16.3184 12.627C16.3184 15.5078 14.9023 16.9727 12.3828 16.9727Z", fillAlpha = 0.85f)
        }
        return _sFDCircle!!
    }

private var _sFDCircle: ImageVector? = null
