package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPersonCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFPersonCircleFill: ImageVector
    get() {
        if (_sFPersonCircleFill != null) {
            return _sFPersonCircleFill!!
        }
        _sFPersonCircleFill = sfIcon(
            name = "Dualtone.SFPersonCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.459C19.7266 25.459 25.4395 19.7461 25.4395 12.7344C25.4395 5.73242 19.7266 0.0195312 12.7148 0.0195312C5.71289 0.0195312 0 5.73242 0 12.7344C0 19.7461 5.71289 25.459 12.7148 25.459Z", fillAlpha = 0.2125f)
            addSfPath("M6.75781 19.3164C6.28906 19.3164 6.07422 19.0137 6.07422 18.584C6.07422 17.168 8.20312 13.4473 12.7148 13.4473C17.2363 13.4473 19.3555 17.168 19.3555 18.584C19.3555 19.0137 19.1504 19.3164 18.6719 19.3164ZM12.7148 12.3926C10.8887 12.3828 9.41406 10.8398 9.41406 8.7793C9.41406 6.85547 10.8887 5.25391 12.7148 5.25391C14.541 5.25391 16.0156 6.85547 16.0156 8.7793C16.0156 10.8398 14.541 12.4023 12.7148 12.3926Z", fillAlpha = 0.85f)
        }
        return _sFPersonCircleFill!!
    }

private var _sFPersonCircleFill: ImageVector? = null
