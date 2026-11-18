package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SF9CircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SF9CircleFill: ImageVector
    get() {
        if (_sF9CircleFill != null) {
            return _sF9CircleFill!!
        }
        _sF9CircleFill = sfIcon(
            name = "Monochrome.SF9CircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM8.34961 10.6836C8.34961 13.0371 10.127 14.4727 12.1582 14.4727C13.877 14.4727 14.9414 13.6328 15.3906 12.5781L15.4785 12.5781C15.498 15.6152 14.2676 17.3633 12.3926 17.3633C11.4551 17.3633 10.7715 16.9434 10.2539 16.1816C10.0488 15.8984 9.83398 15.7129 9.47266 15.7129C9.04297 15.7129 8.73047 16.0156 8.73047 16.4453C8.73047 16.582 8.75977 16.709 8.80859 16.8359C9.0918 17.7148 10.3418 18.8086 12.4023 18.8086C15.4297 18.8086 17.0996 16.6016 17.0996 12.2363C17.0996 8.57422 15.4199 6.64062 12.6367 6.64062C10.0586 6.64062 8.34961 8.24219 8.34961 10.6836ZM15.3125 10.625C15.3125 12.0801 14.209 13.1738 12.6855 13.1738C11.1523 13.1738 10.0684 12.0898 10.0684 10.6055C10.0684 9.04297 11.1426 7.98828 12.6953 7.98828C14.1797 7.98828 15.3125 9.11133 15.3125 10.625Z", fillAlpha = 0.85f)
        }
        return _sF9CircleFill!!
    }

private var _sF9CircleFill: ImageVector? = null
