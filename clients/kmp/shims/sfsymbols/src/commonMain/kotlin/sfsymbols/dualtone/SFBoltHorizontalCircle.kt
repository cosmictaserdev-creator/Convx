package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBoltHorizontalCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFBoltHorizontalCircle: ImageVector
    get() {
        if (_sFBoltHorizontalCircle != null) {
            return _sFBoltHorizontalCircle!!
        }
        _sFBoltHorizontalCircle = sfIcon(
            name = "Dualtone.SFBoltHorizontalCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M4.61914 15.2246C4.11133 15.8887 4.76562 16.4746 5.40039 16.1328L9.61914 13.8184L15.2246 16.8262C15.4297 16.9434 15.6348 17.0117 15.8105 17.0117C16.0645 17.0117 16.2988 16.8848 16.5137 16.6113L20.791 11.0742C21.2598 10.4688 20.6934 9.83398 20.0195 10.1953L15.791 12.5L10.1855 9.48242C9.98047 9.375 9.78516 9.30664 9.59961 9.30664C9.35547 9.30664 9.13086 9.43359 8.90625 9.70703Z", fillAlpha = 0.85f)
        }
        return _sFBoltHorizontalCircle!!
    }

private var _sFBoltHorizontalCircle: ImageVector? = null
