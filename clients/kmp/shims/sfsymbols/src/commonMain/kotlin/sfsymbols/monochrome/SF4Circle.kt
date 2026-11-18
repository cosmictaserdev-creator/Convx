package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SF4Circle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SF4Circle: ImageVector
    get() {
        if (_sF4Circle != null) {
            return _sF4Circle!!
        }
        _sF4Circle = sfIcon(
            name = "Monochrome.SF4Circle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M14.3066 18.6621C14.8242 18.6621 15.1172 18.3301 15.1172 17.7539L15.1172 15.9961L16.2109 15.9961C16.6406 15.9961 16.9336 15.7031 16.9336 15.2832C16.9336 14.8535 16.6504 14.5605 16.2109 14.5605L15.1172 14.5605L15.1172 7.93945C15.1172 7.24609 14.5898 6.78711 13.8086 6.78711C13.0566 6.78711 12.5879 7.05078 12.1387 7.72461C10.8789 9.60938 9.08203 12.2754 8.03711 13.9648C7.83203 14.3164 7.74414 14.5898 7.74414 14.9121C7.74414 15.5664 8.19336 15.9961 8.90625 15.9961L13.5156 15.9961L13.5156 17.7539C13.5156 18.3203 13.8184 18.6621 14.3066 18.6621ZM13.5156 14.5605L9.36523 14.5605L9.36523 14.4922C10.2832 12.9102 12.207 10.0586 13.4277 8.23242L13.5156 8.23242Z", fillAlpha = 0.85f)
        }
        return _sF4Circle!!
    }

private var _sF4Circle: ImageVector? = null
