package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLocationCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFLocationCircleFill: ImageVector
    get() {
        if (_sFLocationCircleFill != null) {
            return _sFLocationCircleFill!!
        }
        _sFLocationCircleFill = sfIcon(
            name = "Dualtone.SFLocationCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M5.61523 13.6719C4.61914 13.6719 4.49219 12.5977 5.24414 12.2363L17.666 6.35742C18.5645 5.9375 19.3262 6.67969 18.9062 7.58789L13.0566 20.0195C12.7148 20.752 11.6309 20.6641 11.6309 19.6289L11.6211 14.0332C11.6211 13.8184 11.4844 13.6816 11.2793 13.6816Z", fillAlpha = 0.85f)
        }
        return _sFLocationCircleFill!!
    }

private var _sFLocationCircleFill: ImageVector? = null
