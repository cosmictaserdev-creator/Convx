package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDivideCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFDivideCircleFill: ImageVector
    get() {
        if (_sFDivideCircleFill != null) {
            return _sFDivideCircleFill!!
        }
        _sFDivideCircleFill = sfIcon(
            name = "Monochrome.SFDivideCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM11.3281 17.5C11.3281 18.2227 11.9336 18.7891 12.6953 18.7891C13.5254 18.7891 14.1113 18.2129 14.1113 17.5C14.1113 16.6895 13.5352 16.1133 12.6953 16.1133C11.9336 16.1133 11.3281 16.6895 11.3281 17.5ZM7.50977 11.8164C6.875 11.8164 6.46484 12.1484 6.46484 12.7441C6.46484 13.3398 6.89453 13.6621 7.50977 13.6621L17.9297 13.6621C18.5449 13.6621 18.9551 13.3398 18.9551 12.7441C18.9551 12.1484 18.5645 11.8164 17.9297 11.8164ZM11.3281 8.08594C11.3281 8.79883 11.9336 9.35547 12.6953 9.35547C13.5254 9.35547 14.1113 8.79883 14.1113 8.07617C14.1113 7.26562 13.5352 6.68945 12.6953 6.68945C11.9336 6.68945 11.3281 7.27539 11.3281 8.08594Z", fillAlpha = 0.85f)
        }
        return _sFDivideCircleFill!!
    }

private var _sFDivideCircleFill: ImageVector? = null
