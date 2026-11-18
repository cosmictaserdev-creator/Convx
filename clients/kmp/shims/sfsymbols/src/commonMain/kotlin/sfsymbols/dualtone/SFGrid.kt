package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFGrid (dualtone)
 * Viewport: 24.5508 x 23.916
 */
public val SfSymbols.Dualtone.SFGrid: ImageVector
    get() {
        if (_sFGrid != null) {
            return _sFGrid!!
        }
        _sFGrid = sfIcon(
            name = "Dualtone.SFGrid",
            viewportWidth = 24.5508f,
            viewportHeight = 23.916f
        ) {
            addSfPath("M7.53906 23.7988C8.03711 23.7988 8.34961 23.4766 8.34961 22.9395L8.34961 0.859375C8.34961 0.322266 8.03711 0 7.53906 0C7.02148 0 6.71875 0.322266 6.71875 0.859375L6.71875 22.9395C6.71875 23.4766 7.02148 23.7988 7.53906 23.7988ZM16.5527 23.7988C17.041 23.7988 17.3633 23.4766 17.3633 22.9395L17.3633 0.859375C17.3633 0.322266 17.041 0 16.5527 0C16.0352 0 15.7324 0.322266 15.7324 0.859375L15.7324 22.9395C15.7324 23.4766 16.0352 23.7988 16.5527 23.7988ZM0.859375 17.1484L23.3301 17.1484C23.8574 17.1484 24.1895 16.8359 24.1895 16.3281C24.1895 15.8203 23.8574 15.5176 23.3301 15.5176L0.859375 15.5176C0.322266 15.5176 0 15.8203 0 16.3281C0 16.8359 0.322266 17.1484 0.859375 17.1484ZM0.859375 8.32031L23.3301 8.32031C23.8574 8.32031 24.1895 8.00781 24.1895 7.5C24.1895 6.99219 23.8574 6.68945 23.3301 6.68945L0.859375 6.68945C0.322266 6.68945 0 6.99219 0 7.5C0 8.00781 0.322266 8.32031 0.859375 8.32031Z", fillAlpha = 0.85f)
        }
        return _sFGrid!!
    }

private var _sFGrid: ImageVector? = null
