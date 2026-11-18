package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFWindowShadeOpen (monochrome)
 * Viewport: 21.4844 x 26.4551
 */
public val SfSymbols.Monochrome.SFWindowShadeOpen: ImageVector
    get() {
        if (_sFWindowShadeOpen != null) {
            return _sFWindowShadeOpen!!
        }
        _sFWindowShadeOpen = sfIcon(
            name = "Monochrome.SFWindowShadeOpen",
            viewportWidth = 21.4844f,
            viewportHeight = 26.4551f
        ) {
            addSfPath("M0 24.1602C0 25.5176 0.9375 26.4355 2.34375 26.4355L18.7891 26.4355C20.1953 26.4355 21.123 25.5176 21.123 24.1602L21.123 2.28516C21.123 0.917969 20.1953 0 18.7891 0L2.34375 0C0.9375 0 0 0.917969 0 2.28516ZM1.73828 24.0332L1.73828 2.40234C1.73828 2.00195 1.99219 1.73828 2.37305 1.73828L18.75 1.73828C19.1309 1.73828 19.3945 2.00195 19.3945 2.40234L19.3945 24.0332C19.3945 24.4336 19.1309 24.707 18.75 24.707L2.37305 24.707C1.99219 24.707 1.73828 24.4336 1.73828 24.0332Z", fillAlpha = 0.85f)
            addSfPath("M3.4668 7.38281L17.666 7.38281C17.8711 7.38281 18.0273 7.2168 18.0273 7.01172L18.0273 3.4668C18.0273 3.26172 17.8711 3.0957 17.666 3.0957L3.4668 3.0957C3.26172 3.0957 3.0957 3.26172 3.0957 3.4668L3.0957 7.01172C3.0957 7.2168 3.26172 7.38281 3.4668 7.38281ZM8.58398 5.82031C8.24219 5.82031 7.99805 5.57617 7.99805 5.23438C7.99805 4.89258 8.24219 4.6582 8.58398 4.6582L12.5488 4.6582C12.8906 4.6582 13.125 4.89258 13.125 5.23438C13.125 5.57617 12.8906 5.82031 12.5488 5.82031Z", fillAlpha = 0.85f)
        }
        return _sFWindowShadeOpen!!
    }

private var _sFWindowShadeOpen: ImageVector? = null
