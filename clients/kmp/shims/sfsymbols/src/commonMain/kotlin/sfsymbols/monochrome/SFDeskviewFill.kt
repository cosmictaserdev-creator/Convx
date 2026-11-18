package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDeskviewFill (monochrome)
 * Viewport: 25.5078 x 30.3027
 */
public val SfSymbols.Monochrome.SFDeskviewFill: ImageVector
    get() {
        if (_sFDeskviewFill != null) {
            return _sFDeskviewFill!!
        }
        _sFDeskviewFill = sfIcon(
            name = "Monochrome.SFDeskviewFill",
            viewportWidth = 25.5078f,
            viewportHeight = 30.3027f
        ) {
            addSfPath("M0.917969 30.1367L24.2188 30.1367C24.7461 30.1367 25.1465 29.7266 25.1465 29.209C25.1465 28.6914 24.7363 28.2812 24.2188 28.2812L0.917969 28.2812C0.400391 28.2812 0 28.6914 0 29.209C0 29.7266 0.400391 30.1367 0.917969 30.1367Z", fillAlpha = 0.85f)
            addSfPath("M8.19336 14.5898L16.9629 14.5898C18.7598 14.5898 19.8047 13.5938 19.8047 11.7969L19.8047 2.88086C19.8047 1.12305 18.7598 0 16.9629 0L8.18359 0C6.39648 0 5.3418 1.05469 5.3418 2.88086L5.3418 11.7285C5.3418 13.5352 6.39648 14.5898 8.19336 14.5898ZM7.80273 21.2793L17.3438 21.2793C18.0371 21.2793 18.5059 20.8398 18.5059 20.1855C18.5059 19.8633 18.3594 19.502 18.1055 19.1992L15.3418 15.957L9.79492 15.957L7.08008 19.1992C6.80664 19.5117 6.64062 19.8633 6.64062 20.1855C6.64062 20.8398 7.09961 21.2793 7.80273 21.2793Z", fillAlpha = 0.85f)
        }
        return _sFDeskviewFill!!
    }

private var _sFDeskviewFill: ImageVector? = null
