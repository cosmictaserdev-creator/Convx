package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBalloon (monochrome)
 * Viewport: 16.1621 x 33.6035
 */
public val SfSymbols.Monochrome.SFBalloon: ImageVector
    get() {
        if (_sFBalloon != null) {
            return _sFBalloon!!
        }
        _sFBalloon = sfIcon(
            name = "Monochrome.SFBalloon",
            viewportWidth = 16.1621f,
            viewportHeight = 33.6035f
        ) {
            addSfPath("M7.89062 18.7988C12.334 18.7988 15.8008 14.082 15.8008 8.13477C15.8008 3.4668 12.5879 0 7.89062 0C3.20312 0 0 3.4668 0 8.13477C0 14.082 3.44727 18.7988 7.89062 18.7988ZM7.89062 17.1777C4.33594 17.1777 1.62109 13.0566 1.62109 8.13477C1.62109 4.50195 4.20898 1.63086 7.89062 1.63086C11.5625 1.63086 14.1797 4.50195 14.1797 8.13477C14.1797 13.0566 11.4355 17.1777 7.89062 17.1777ZM6.2793 18.0664L5.72266 20.1465C5.58594 20.5957 5.84961 21.0547 6.40625 21.0547L9.39453 21.0547C9.95117 21.0547 10.2051 20.5957 10.0781 20.1465L9.51172 18.0664ZM7.65625 33.4473C8.08594 33.4473 8.4375 33.0957 8.4375 32.6758C8.4375 29.873 10.957 29.1895 10.957 26.4453C10.957 23.6816 8.60352 23.0176 8.62305 20.5176L7.08008 20.5176C7.07031 23.8477 9.42383 24.375 9.42383 26.4453C9.42383 28.5645 6.89453 28.877 6.89453 32.6758C6.89453 33.0957 7.23633 33.4473 7.65625 33.4473Z", fillAlpha = 0.85f)
        }
        return _sFBalloon!!
    }

private var _sFBalloon: ImageVector? = null
