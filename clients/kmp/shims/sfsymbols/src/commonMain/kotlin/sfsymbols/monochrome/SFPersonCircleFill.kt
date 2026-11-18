package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPersonCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFPersonCircleFill: ImageVector
    get() {
        if (_sFPersonCircleFill != null) {
            return _sFPersonCircleFill!!
        }
        _sFPersonCircleFill = sfIcon(
            name = "Monochrome.SFPersonCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7344C25.4395 19.7461 19.7266 25.459 12.7148 25.459C5.71289 25.459 0 19.7461 0 12.7344C0 5.73242 5.71289 0.0195312 12.7148 0.0195312C19.7266 0.0195312 25.4395 5.73242 25.4395 12.7344ZM6.07422 18.584C6.07422 19.0137 6.28906 19.3164 6.75781 19.3164L18.6719 19.3164C19.1504 19.3164 19.3555 19.0137 19.3555 18.584C19.3555 17.168 17.2363 13.4473 12.7148 13.4473C8.20312 13.4473 6.07422 17.168 6.07422 18.584ZM9.41406 8.7793C9.41406 10.8398 10.8887 12.3828 12.7148 12.3926C14.541 12.4023 16.0156 10.8398 16.0156 8.7793C16.0156 6.85547 14.541 5.25391 12.7148 5.25391C10.8887 5.25391 9.41406 6.85547 9.41406 8.7793Z", fillAlpha = 0.85f)
        }
        return _sFPersonCircleFill!!
    }

private var _sFPersonCircleFill: ImageVector? = null
