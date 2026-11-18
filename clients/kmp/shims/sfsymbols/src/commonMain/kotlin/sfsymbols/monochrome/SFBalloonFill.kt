package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBalloonFill (monochrome)
 * Viewport: 16.1621 x 33.6035
 */
public val SfSymbols.Monochrome.SFBalloonFill: ImageVector
    get() {
        if (_sFBalloonFill != null) {
            return _sFBalloonFill!!
        }
        _sFBalloonFill = sfIcon(
            name = "Monochrome.SFBalloonFill",
            viewportWidth = 16.1621f,
            viewportHeight = 33.6035f
        ) {
            addSfPath("M7.89062 18.7988C12.334 18.7988 15.8008 14.082 15.8008 8.13477C15.8008 3.4668 12.5879 0 7.89062 0C3.20312 0 0 3.4668 0 8.13477C0 14.082 3.44727 18.7988 7.89062 18.7988ZM6.2793 18.0664L5.72266 20.1465C5.60547 20.5957 5.84961 21.0547 6.40625 21.0547L9.39453 21.0547C9.95117 21.0547 10.2051 20.5957 10.0781 20.1465L9.51172 18.0664ZM7.65625 33.4473C8.08594 33.4473 8.4375 33.0957 8.4375 32.6758C8.4375 29.873 10.957 29.1895 10.957 26.4453C10.957 23.6816 8.60352 23.0176 8.62305 20.5176L7.08008 20.5176C7.07031 23.8477 9.42383 24.375 9.42383 26.4453C9.42383 28.5645 6.89453 28.877 6.89453 32.6758C6.89453 33.0957 7.23633 33.4473 7.65625 33.4473Z", fillAlpha = 0.85f)
        }
        return _sFBalloonFill!!
    }

private var _sFBalloonFill: ImageVector? = null
