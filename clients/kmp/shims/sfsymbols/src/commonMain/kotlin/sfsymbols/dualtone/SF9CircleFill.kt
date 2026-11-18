package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SF9CircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SF9CircleFill: ImageVector
    get() {
        if (_sF9CircleFill != null) {
            return _sF9CircleFill!!
        }
        _sF9CircleFill = sfIcon(
            name = "Dualtone.SF9CircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M12.4023 18.8086C10.3418 18.8086 9.0918 17.7148 8.80859 16.8359C8.75977 16.709 8.73047 16.582 8.73047 16.4453C8.73047 16.0156 9.04297 15.7129 9.47266 15.7129C9.83398 15.7129 10.0488 15.8984 10.2539 16.1816C10.7715 16.9434 11.4551 17.3633 12.3926 17.3633C14.2676 17.3633 15.498 15.6152 15.4785 12.5781L15.3906 12.5781C14.9414 13.6328 13.877 14.4727 12.1582 14.4727C10.127 14.4727 8.34961 13.0371 8.34961 10.6836C8.34961 8.24219 10.0586 6.64062 12.6367 6.64062C15.4199 6.64062 17.0996 8.57422 17.0996 12.2363C17.0996 16.6016 15.4297 18.8086 12.4023 18.8086ZM12.6855 13.1738C14.209 13.1738 15.3125 12.0801 15.3125 10.625C15.3125 9.11133 14.1797 7.98828 12.6953 7.98828C11.1426 7.98828 10.0684 9.04297 10.0684 10.6055C10.0684 12.0898 11.1523 13.1738 12.6855 13.1738Z", fillAlpha = 0.85f)
        }
        return _sF9CircleFill!!
    }

private var _sF9CircleFill: ImageVector? = null
