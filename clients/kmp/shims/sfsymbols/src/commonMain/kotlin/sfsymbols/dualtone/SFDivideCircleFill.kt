package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDivideCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFDivideCircleFill: ImageVector
    get() {
        if (_sFDivideCircleFill != null) {
            return _sFDivideCircleFill!!
        }
        _sFDivideCircleFill = sfIcon(
            name = "Dualtone.SFDivideCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M12.6953 9.35547C11.9336 9.35547 11.3281 8.79883 11.3281 8.08594C11.3281 7.27539 11.9336 6.68945 12.6953 6.68945C13.5352 6.68945 14.1113 7.26562 14.1113 8.07617C14.1113 8.79883 13.5254 9.35547 12.6953 9.35547ZM7.50977 13.6621C6.89453 13.6621 6.46484 13.3398 6.46484 12.7441C6.46484 12.1484 6.875 11.8164 7.50977 11.8164L17.9297 11.8164C18.5645 11.8164 18.9551 12.1484 18.9551 12.7441C18.9551 13.3398 18.5449 13.6621 17.9297 13.6621ZM12.6953 18.7891C11.9336 18.7891 11.3281 18.2227 11.3281 17.5C11.3281 16.6895 11.9336 16.1133 12.6953 16.1133C13.5352 16.1133 14.1113 16.6895 14.1113 17.5C14.1113 18.2129 13.5254 18.7891 12.6953 18.7891Z", fillAlpha = 0.85f)
        }
        return _sFDivideCircleFill!!
    }

private var _sFDivideCircleFill: ImageVector? = null
