package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFBCircleFill: ImageVector
    get() {
        if (_sFBCircleFill != null) {
            return _sFBCircleFill!!
        }
        _sFBCircleFill = sfIcon(
            name = "Dualtone.SFBCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M9.56055 18.5059C8.94531 18.5059 8.57422 18.1348 8.57422 17.5098L8.57422 7.75391C8.57422 7.12891 8.94531 6.75781 9.56055 6.75781L13.3398 6.75781C15.498 6.75781 16.9238 7.91016 16.9238 9.6582C16.9238 10.9375 16.1426 11.9629 14.9023 12.2656L14.9023 12.334C16.582 12.5488 17.627 13.6523 17.627 15.2148C17.627 17.2363 15.9473 18.5059 13.3301 18.5059ZM10.3125 11.8262L12.5 11.8262C14.209 11.8262 15.2051 11.1133 15.2051 9.94141C15.2051 8.78906 14.3848 8.10547 12.9883 8.10547L10.3125 8.10547ZM10.3125 17.1582L12.6172 17.1582C14.8047 17.1582 15.8398 16.5137 15.8398 15.166C15.8398 13.8574 14.873 13.0957 13.252 13.0957L10.3125 13.0957Z", fillAlpha = 0.85f)
        }
        return _sFBCircleFill!!
    }

private var _sFBCircleFill: ImageVector? = null
