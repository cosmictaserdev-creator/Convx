package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFWindowShadeClosed (monochrome)
 * Viewport: 21.4844 x 26.4551
 */
public val SfSymbols.Monochrome.SFWindowShadeClosed: ImageVector
    get() {
        if (_sFWindowShadeClosed != null) {
            return _sFWindowShadeClosed!!
        }
        _sFWindowShadeClosed = sfIcon(
            name = "Monochrome.SFWindowShadeClosed",
            viewportWidth = 21.4844f,
            viewportHeight = 26.4551f
        ) {
            addSfPath("M0 24.1602C0 25.5176 0.9375 26.4355 2.34375 26.4355L18.7891 26.4355C20.1953 26.4355 21.123 25.5176 21.123 24.1602L21.123 2.28516C21.123 0.917969 20.1953 0 18.7891 0L2.34375 0C0.9375 0 0 0.917969 0 2.28516ZM1.73828 24.0332L1.73828 2.40234C1.73828 2.00195 1.99219 1.73828 2.37305 1.73828L18.75 1.73828C19.1309 1.73828 19.3945 2.00195 19.3945 2.40234L19.3945 24.0332C19.3945 24.4336 19.1309 24.707 18.75 24.707L2.37305 24.707C1.99219 24.707 1.73828 24.4336 1.73828 24.0332Z", fillAlpha = 0.85f)
            addSfPath("M3.4668 23.3398L17.666 23.3398C17.8711 23.3398 18.0273 23.1836 18.0273 22.9785L18.0273 3.4668C18.0273 3.26172 17.8711 3.0957 17.666 3.0957L3.4668 3.0957C3.26172 3.0957 3.0957 3.26172 3.0957 3.4668L3.0957 22.9785C3.0957 23.1836 3.26172 23.3398 3.4668 23.3398ZM8.39844 21.7871C8.05664 21.7871 7.8125 21.543 7.8125 21.2012C7.8125 20.8594 8.05664 20.625 8.39844 20.625L12.3633 20.625C12.7051 20.625 12.9395 20.8594 12.9395 21.2012C12.9395 21.543 12.7051 21.7871 12.3633 21.7871Z", fillAlpha = 0.85f)
        }
        return _sFWindowShadeClosed!!
    }

private var _sFWindowShadeClosed: ImageVector? = null
