package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPersonCropCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFPersonCropCircleFill: ImageVector
    get() {
        if (_sFPersonCropCircleFill != null) {
            return _sFPersonCropCircleFill!!
        }
        _sFPersonCropCircleFill = sfIcon(
            name = "Dualtone.SFPersonCropCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M12.7148 23.8086C9.66797 23.8086 6.73828 22.5195 4.82422 20.4785C6.19141 18.3008 9.24805 16.9727 12.7148 16.9727C16.1523 16.9727 19.2188 18.2812 20.6055 20.4785C18.6914 22.5195 15.752 23.8086 12.7148 23.8086ZM12.7148 14.834C10.293 14.8145 8.4082 12.7832 8.4082 10.0781C8.38867 7.53906 10.3125 5.42969 12.7148 5.42969C15.1172 5.42969 17.0215 7.53906 17.0215 10.0781C17.0215 12.7832 15.1367 14.8535 12.7148 14.834Z", fillAlpha = 0.85f)
        }
        return _sFPersonCropCircleFill!!
    }

private var _sFPersonCropCircleFill: ImageVector? = null
