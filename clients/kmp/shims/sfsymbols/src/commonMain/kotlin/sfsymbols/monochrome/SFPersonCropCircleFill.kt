package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPersonCropCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFPersonCropCircleFill: ImageVector
    get() {
        if (_sFPersonCropCircleFill != null) {
            return _sFPersonCropCircleFill!!
        }
        _sFPersonCropCircleFill = sfIcon(
            name = "Monochrome.SFPersonCropCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM4.82422 20.4785C6.73828 22.5195 9.66797 23.8086 12.7148 23.8086C15.752 23.8086 18.6914 22.5195 20.6055 20.4785C19.2188 18.2812 16.1523 16.9727 12.7148 16.9727C9.24805 16.9727 6.19141 18.3008 4.82422 20.4785ZM8.4082 10.0781C8.4082 12.7832 10.293 14.8145 12.7148 14.834C15.1367 14.8535 17.0215 12.7832 17.0215 10.0781C17.0215 7.53906 15.1172 5.42969 12.7148 5.42969C10.3125 5.42969 8.38867 7.53906 8.4082 10.0781Z", fillAlpha = 0.85f)
        }
        return _sFPersonCropCircleFill!!
    }

private var _sFPersonCropCircleFill: ImageVector? = null
