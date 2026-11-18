package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFVCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFVCircle: ImageVector
    get() {
        if (_sFVCircle != null) {
            return _sFVCircle!!
        }
        _sFVCircle = sfIcon(
            name = "Dualtone.SFVCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M12.7148 18.5645C13.3496 18.5645 13.75 18.2324 14.0039 17.5488L17.6172 8.0957C17.6855 7.91016 17.7051 7.7832 17.7051 7.61719C17.7051 7.13867 17.3438 6.81641 16.8359 6.81641C16.3672 6.81641 16.1035 7.03125 15.918 7.54883L12.7441 16.5039L12.666 16.5039L9.49219 7.54883C9.31641 7.02148 9.04297 6.81641 8.57422 6.81641C8.06641 6.81641 7.70508 7.14844 7.70508 7.61719C7.70508 7.7832 7.72461 7.91016 7.79297 8.0957L11.416 17.5586C11.6895 18.2812 12.0508 18.5645 12.7148 18.5645Z", fillAlpha = 0.85f)
        }
        return _sFVCircle!!
    }

private var _sFVCircle: ImageVector? = null
