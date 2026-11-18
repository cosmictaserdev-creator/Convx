package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFUCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFUCircle: ImageVector
    get() {
        if (_sFUCircle != null) {
            return _sFUCircle!!
        }
        _sFUCircle = sfIcon(
            name = "Monochrome.SFUCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M12.7051 18.5938C15.5371 18.5938 17.4805 16.9336 17.4805 14.3457L17.4805 7.76367C17.4805 7.1582 17.1582 6.80664 16.6113 6.80664C16.0742 6.80664 15.752 7.1582 15.752 7.76367L15.752 14.1895C15.752 15.9863 14.5215 17.1484 12.7051 17.1484C10.8887 17.1484 9.6582 15.9863 9.6582 14.1895L9.6582 7.76367C9.6582 7.1582 9.3457 6.80664 8.78906 6.80664C8.26172 6.80664 7.93945 7.1582 7.93945 7.76367L7.93945 14.3457C7.93945 16.9336 9.87305 18.5938 12.7051 18.5938Z", fillAlpha = 0.85f)
        }
        return _sFUCircle!!
    }

private var _sFUCircle: ImageVector? = null
