package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SF11CircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SF11CircleFill: ImageVector
    get() {
        if (_sF11CircleFill != null) {
            return _sF11CircleFill!!
        }
        _sF11CircleFill = sfIcon(
            name = "Monochrome.SF11CircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM8.96484 7.8125L7.46094 9.01367C7.25586 9.16992 7.10938 9.38477 7.10938 9.67773C7.10938 10.0391 7.35352 10.293 7.70508 10.293C7.88086 10.293 7.99805 10.2344 8.10547 10.1367L9.39453 9.11133L9.39453 17.2363C9.39453 17.7051 9.77539 18.0762 10.2246 18.0762C10.6836 18.0762 11.0547 17.7051 11.0547 17.2363L11.0547 8.27148C11.0547 7.69531 10.6934 7.34375 10.1074 7.34375C9.62891 7.34375 9.23828 7.60742 8.96484 7.8125ZM14.6973 7.8125L13.1934 9.01367C12.998 9.16992 12.8418 9.38477 12.8418 9.67773C12.8418 10.0391 13.0859 10.293 13.4277 10.293C13.6035 10.293 13.7207 10.2344 13.8379 10.1367L15.127 9.11133L15.127 17.2363C15.127 17.7051 15.498 18.0762 15.957 18.0762C16.4062 18.0762 16.7871 17.7051 16.7871 17.2363L16.7871 8.27148C16.7871 7.69531 16.416 7.34375 15.8398 7.34375C15.3516 7.34375 14.9707 7.60742 14.6973 7.8125Z", fillAlpha = 0.85f)
        }
        return _sF11CircleFill!!
    }

private var _sF11CircleFill: ImageVector? = null
