package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTengesignCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFTengesignCircleFill: ImageVector
    get() {
        if (_sFTengesignCircleFill != null) {
            return _sFTengesignCircleFill!!
        }
        _sFTengesignCircleFill = sfIcon(
            name = "Monochrome.SFTengesignCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM8.88672 9.30664C8.57422 9.30664 8.34961 9.54102 8.34961 9.83398C8.34961 10.127 8.57422 10.3613 8.88672 10.3613L12.0605 10.3613L12.0605 17.9883C12.0605 18.3887 12.3535 18.6816 12.7344 18.6816C13.1152 18.6816 13.3984 18.3887 13.3984 17.9883L13.3984 10.3613L16.5527 10.3613C16.8652 10.3613 17.0898 10.127 17.0898 9.83398C17.0898 9.54102 16.8652 9.30664 16.5527 9.30664ZM8.71094 7.01172C8.49609 7.01172 8.31055 7.19727 8.31055 7.41211C8.31055 7.63672 8.49609 7.80273 8.71094 7.80273L16.6895 7.80273C16.9043 7.80273 17.0898 7.61719 17.0898 7.41211C17.0898 7.19727 16.9043 7.01172 16.6895 7.01172Z", fillAlpha = 0.85f)
        }
        return _sFTengesignCircleFill!!
    }

private var _sFTengesignCircleFill: ImageVector? = null
