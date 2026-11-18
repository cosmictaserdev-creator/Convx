package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRightCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFRightCircleFill: ImageVector
    get() {
        if (_sFRightCircleFill != null) {
            return _sFRightCircleFill!!
        }
        _sFRightCircleFill = sfIcon(
            name = "Monochrome.SFRightCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM9.4043 6.78711C8.84766 6.78711 8.54492 7.19727 8.54492 7.79297L8.54492 17.627C8.54492 18.2129 8.85742 18.6133 9.42383 18.6133C10 18.6133 10.3223 18.2324 10.3223 17.627L10.3223 14.1504L12.9004 14.1504L15.2246 18.0176C15.4883 18.4473 15.7129 18.6133 16.1133 18.6133C16.5918 18.6133 16.9238 18.3008 16.9238 17.8516C16.9238 17.5977 16.8555 17.4219 16.709 17.1875L14.6582 13.8965C16.1914 13.4375 17.1875 12.1094 17.1875 10.4883C17.1875 8.24219 15.5762 6.78711 13.0859 6.78711ZM15.4297 10.5273C15.4297 11.9629 14.4141 12.8125 12.8516 12.8125L10.3223 12.8125L10.3223 8.17383L12.8906 8.17383C14.3945 8.17383 15.4297 9.05273 15.4297 10.5273Z", fillAlpha = 0.85f)
        }
        return _sFRightCircleFill!!
    }

private var _sFRightCircleFill: ImageVector? = null
