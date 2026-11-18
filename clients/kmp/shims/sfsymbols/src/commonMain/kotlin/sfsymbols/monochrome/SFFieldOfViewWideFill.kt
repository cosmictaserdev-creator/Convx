package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFFieldOfViewWideFill (monochrome)
 * Viewport: 29.3831 x 21.8262
 */
public val SfSymbols.Monochrome.SFFieldOfViewWideFill: ImageVector
    get() {
        if (_sFFieldOfViewWideFill != null) {
            return _sFFieldOfViewWideFill!!
        }
        _sFFieldOfViewWideFill = sfIcon(
            name = "Monochrome.SFFieldOfViewWideFill",
            viewportWidth = 29.3831f,
            viewportHeight = 21.8262f
        ) {
            addSfPath("M0.73157 8.83789L12.3136 20.3711C13.7394 21.7871 15.2921 21.7871 16.7374 20.3711L28.2902 8.83789C29.3058 7.82227 29.2179 6.5625 28.2804 5.57617C24.9894 2.13867 19.755 0 14.5109 0C9.26673 0 4.04212 2.14844 0.741335 5.57617C-0.20593 6.55273-0.284055 7.82227 0.73157 8.83789Z", fillAlpha = 0.85f)
        }
        return _sFFieldOfViewWideFill!!
    }

private var _sFFieldOfViewWideFill: ImageVector? = null
