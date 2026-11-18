package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SF6CircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SF6CircleFill: ImageVector
    get() {
        if (_sF6CircleFill != null) {
            return _sF6CircleFill!!
        }
        _sF6CircleFill = sfIcon(
            name = "Monochrome.SF6CircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM8.32031 13.0273C8.32031 14.7461 8.61328 16.0352 9.13086 16.875C9.92188 18.2227 11.3086 18.8086 12.8125 18.8086C15.3516 18.8086 17.1191 17.2852 17.1191 14.7266C17.1191 12.3535 15.332 10.9473 13.2812 10.9473C11.5527 10.9473 10.5078 11.8164 10.0488 12.832C10.0195 12.832 9.99023 12.832 9.96094 12.832C9.96094 12.7734 9.96094 12.7344 9.96094 12.6855C9.96094 9.85352 11.1914 8.07617 13.0566 8.07617C14.0039 8.07617 14.6289 8.50586 15.0098 9.0625C15.3516 9.42383 15.5664 9.62891 15.957 9.62891C16.3379 9.62891 16.6406 9.35547 16.6406 8.95508C16.6406 8.7793 16.6016 8.63281 16.5332 8.4668C16.1816 7.54883 14.8145 6.64062 13.1055 6.64062C10.1465 6.64062 8.32031 8.80859 8.32031 13.0273ZM15.3711 14.8145C15.3711 16.4062 14.3359 17.4414 12.7441 17.4414C11.2305 17.4414 10.127 16.3281 10.127 14.7949C10.127 13.3203 11.2207 12.2461 12.7637 12.2461C14.3066 12.2461 15.3711 13.3008 15.3711 14.8145Z", fillAlpha = 0.85f)
        }
        return _sF6CircleFill!!
    }

private var _sF6CircleFill: ImageVector? = null
