package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLocationCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFLocationCircleFill: ImageVector
    get() {
        if (_sFLocationCircleFill != null) {
            return _sFLocationCircleFill!!
        }
        _sFLocationCircleFill = sfIcon(
            name = "Monochrome.SFLocationCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM17.666 6.35742L5.24414 12.2363C4.49219 12.5977 4.61914 13.6719 5.61523 13.6719L11.2793 13.6816C11.4844 13.6816 11.6211 13.8184 11.6211 14.0332L11.6309 19.6289C11.6309 20.6641 12.7148 20.752 13.0566 20.0195L18.9062 7.58789C19.3262 6.67969 18.5645 5.9375 17.666 6.35742Z", fillAlpha = 0.85f)
        }
        return _sFLocationCircleFill!!
    }

private var _sFLocationCircleFill: ImageVector? = null
