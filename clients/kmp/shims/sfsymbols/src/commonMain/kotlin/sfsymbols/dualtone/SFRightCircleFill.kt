package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRightCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFRightCircleFill: ImageVector
    get() {
        if (_sFRightCircleFill != null) {
            return _sFRightCircleFill!!
        }
        _sFRightCircleFill = sfIcon(
            name = "Dualtone.SFRightCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M9.42383 18.6133C8.85742 18.6133 8.54492 18.2129 8.54492 17.627L8.54492 7.79297C8.54492 7.19727 8.84766 6.78711 9.4043 6.78711L13.0859 6.78711C15.5762 6.78711 17.1875 8.24219 17.1875 10.4883C17.1875 12.1094 16.1914 13.4375 14.6582 13.8965L16.709 17.1875C16.8555 17.4219 16.9238 17.5977 16.9238 17.8516C16.9238 18.3008 16.5918 18.6133 16.1133 18.6133C15.7129 18.6133 15.4883 18.4473 15.2246 18.0176L12.9004 14.1504L10.3223 14.1504L10.3223 17.627C10.3223 18.2324 10 18.6133 9.42383 18.6133ZM10.3223 12.8125L12.8516 12.8125C14.4141 12.8125 15.4297 11.9629 15.4297 10.5273C15.4297 9.05273 14.3945 8.17383 12.8906 8.17383L10.3223 8.17383Z", fillAlpha = 0.85f)
        }
        return _sFRightCircleFill!!
    }

private var _sFRightCircleFill: ImageVector? = null
