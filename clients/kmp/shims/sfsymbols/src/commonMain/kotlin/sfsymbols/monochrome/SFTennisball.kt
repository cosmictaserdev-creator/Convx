package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTennisball (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFTennisball: ImageVector
    get() {
        if (_sFTennisball != null) {
            return _sFTennisball!!
        }
        _sFTennisball = sfIcon(
            name = "Monochrome.SFTennisball",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M13.9062 24.7656L15.293 24.4336C15.0879 23.7402 14.9902 22.998 14.9902 22.2168C14.9902 17.7539 18.3691 14.3652 22.8125 14.3652C23.418 14.3652 23.9844 14.4336 24.5215 14.5508L24.873 13.1738C24.209 13.0078 23.5254 12.9395 22.8125 12.9395C17.6758 12.9395 13.5547 17.0508 13.5547 22.2168C13.5547 23.1055 13.6719 23.9648 13.9062 24.7656ZM0.751953 10.791L0.46875 12.1875C1.17188 12.3535 1.9043 12.4512 2.66602 12.4512C7.8125 12.4512 11.9336 8.33008 11.9336 3.16406C11.9336 2.32422 11.8164 1.52344 11.6113 0.751953L10.1758 0.9375C10.3906 1.64062 10.498 2.38281 10.498 3.16406C10.498 7.62695 7.10938 11.0156 2.66602 11.0156C2.00195 11.0156 1.36719 10.9375 0.751953 10.791ZM12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0ZM12.7148 1.48438C18.9258 1.48438 23.9551 6.51367 23.9551 12.7246C23.9551 18.9258 18.9258 23.9551 12.7148 23.9551C6.51367 23.9551 1.48438 18.9258 1.48438 12.7246C1.48438 6.51367 6.51367 1.48438 12.7148 1.48438Z", fillAlpha = 0.85f)
        }
        return _sFTennisball!!
    }

private var _sFTennisball: ImageVector? = null
