package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTengesignCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFTengesignCircleFill: ImageVector
    get() {
        if (_sFTengesignCircleFill != null) {
            return _sFTengesignCircleFill!!
        }
        _sFTengesignCircleFill = sfIcon(
            name = "Dualtone.SFTengesignCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M12.7344 18.6816C12.3535 18.6816 12.0605 18.3887 12.0605 17.9883L12.0605 10.3613L8.88672 10.3613C8.57422 10.3613 8.34961 10.127 8.34961 9.83398C8.34961 9.54102 8.57422 9.30664 8.88672 9.30664L16.5527 9.30664C16.8652 9.30664 17.0898 9.54102 17.0898 9.83398C17.0898 10.127 16.8652 10.3613 16.5527 10.3613L13.3984 10.3613L13.3984 17.9883C13.3984 18.3887 13.1152 18.6816 12.7344 18.6816ZM8.31055 7.41211C8.31055 7.19727 8.49609 7.01172 8.71094 7.01172L16.6895 7.01172C16.9043 7.01172 17.0898 7.19727 17.0898 7.41211C17.0898 7.61719 16.9043 7.80273 16.6895 7.80273L8.71094 7.80273C8.49609 7.80273 8.31055 7.63672 8.31055 7.41211Z", fillAlpha = 0.85f)
        }
        return _sFTengesignCircleFill!!
    }

private var _sFTengesignCircleFill: ImageVector? = null
