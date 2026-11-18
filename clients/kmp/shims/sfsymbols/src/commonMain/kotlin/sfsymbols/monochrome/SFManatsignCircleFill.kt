package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFManatsignCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFManatsignCircleFill: ImageVector
    get() {
        if (_sFManatsignCircleFill != null) {
            return _sFManatsignCircleFill!!
        }
        _sFManatsignCircleFill = sfIcon(
            name = "Monochrome.SFManatsignCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM12.2168 6.74805L12.2168 8.24219C9.58984 8.47656 8.34961 10.6836 8.34961 13.5449L8.34961 17.5879C8.34961 18.0078 8.60352 18.2812 9.01367 18.2812C9.44336 18.2812 9.69727 18.0078 9.69727 17.5879L9.69727 13.6328C9.69727 11.4844 10.4102 9.73633 12.2168 9.46289L12.2168 17.793C12.2168 18.0957 12.4121 18.2812 12.7148 18.2812C13.0078 18.2812 13.1836 18.0957 13.1836 17.793L13.1836 9.45312C15.0879 9.69727 15.7812 11.4844 15.7812 13.6328L15.7812 17.5879C15.7812 18.0078 16.0352 18.2812 16.4551 18.2812C16.875 18.2812 17.1191 18.0078 17.1191 17.5879L17.1191 13.5449C17.1191 10.6348 15.8496 8.45703 13.1836 8.23242L13.1836 6.74805C13.1836 6.44531 13.0078 6.26953 12.7148 6.26953C12.4121 6.26953 12.2168 6.44531 12.2168 6.74805Z", fillAlpha = 0.85f)
        }
        return _sFManatsignCircleFill!!
    }

private var _sFManatsignCircleFill: ImageVector? = null
