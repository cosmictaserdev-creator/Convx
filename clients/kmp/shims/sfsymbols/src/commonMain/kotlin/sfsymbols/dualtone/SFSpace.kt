package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSpace (dualtone)
 * Viewport: 30.0488 x 15.2832
 */
public val SfSymbols.Dualtone.SFSpace: ImageVector
    get() {
        if (_sFSpace != null) {
            return _sFSpace!!
        }
        _sFSpace = sfIcon(
            name = "Dualtone.SFSpace",
            viewportWidth = 30.0488f,
            viewportHeight = 15.2832f
        ) {
            addSfPath("M0 12.793C0 14.3164 0.888672 15.2832 2.5 15.2832L27.1875 15.2832C28.7988 15.2832 29.6875 14.3164 29.6875 12.793L29.6875 8.48633C29.6875 7.99805 29.2871 7.59766 28.7891 7.59766C28.3008 7.59766 27.9004 7.99805 27.9004 8.48633L27.9004 12.7539C27.9004 13.252 27.5293 13.6133 27.0312 13.6133L2.65625 13.6133C2.14844 13.6133 1.78711 13.2617 1.78711 12.7539L1.78711 8.48633C1.78711 7.99805 1.38672 7.59766 0.898438 7.59766C0.400391 7.59766 0 7.99805 0 8.48633Z", fillAlpha = 0.85f)
        }
        return _sFSpace!!
    }

private var _sFSpace: ImageVector? = null
