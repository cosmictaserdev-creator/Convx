package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFBCircleFill: ImageVector
    get() {
        if (_sFBCircleFill != null) {
            return _sFBCircleFill!!
        }
        _sFBCircleFill = sfIcon(
            name = "Monochrome.SFBCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM9.56055 6.75781C8.94531 6.75781 8.57422 7.12891 8.57422 7.75391L8.57422 17.5098C8.57422 18.1348 8.94531 18.5059 9.56055 18.5059L13.3301 18.5059C15.9473 18.5059 17.627 17.2363 17.627 15.2148C17.627 13.6523 16.582 12.5488 14.9023 12.334L14.9023 12.2656C16.1426 11.9629 16.9238 10.9375 16.9238 9.6582C16.9238 7.91016 15.498 6.75781 13.3398 6.75781ZM15.8398 15.166C15.8398 16.5137 14.8047 17.1582 12.6172 17.1582L10.3125 17.1582L10.3125 13.0957L13.252 13.0957C14.873 13.0957 15.8398 13.8574 15.8398 15.166ZM15.2051 9.94141C15.2051 11.1133 14.209 11.8262 12.5 11.8262L10.3125 11.8262L10.3125 8.10547L12.9883 8.10547C14.3848 8.10547 15.2051 8.78906 15.2051 9.94141Z", fillAlpha = 0.85f)
        }
        return _sFBCircleFill!!
    }

private var _sFBCircleFill: ImageVector? = null
