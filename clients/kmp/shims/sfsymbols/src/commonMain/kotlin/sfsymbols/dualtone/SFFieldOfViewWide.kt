package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFFieldOfViewWide (dualtone)
 * Viewport: 29.3831 x 21.8262
 */
public val SfSymbols.Dualtone.SFFieldOfViewWide: ImageVector
    get() {
        if (_sFFieldOfViewWide != null) {
            return _sFFieldOfViewWide!!
        }
        _sFFieldOfViewWide = sfIcon(
            name = "Dualtone.SFFieldOfViewWide",
            viewportWidth = 29.3831f,
            viewportHeight = 21.8262f
        ) {
            addSfPath("M0.73157 8.83789L12.3136 20.3711C13.7394 21.7871 15.2921 21.7871 16.7374 20.3711L28.2902 8.83789C29.3058 7.82227 29.2179 6.5625 28.2804 5.57617C24.9894 2.13867 19.755 0 14.5109 0C9.26673 0 4.04212 2.14844 0.741335 5.57617C-0.20593 6.55273-0.284055 7.82227 0.73157 8.83789ZM2.01087 7.70508C1.67884 7.37305 1.6886 7.03125 2.0011 6.71875C4.97962 3.64258 9.70618 1.72852 14.4718 1.72852C19.2277 1.72852 23.9152 3.68164 26.9327 6.71875C27.2452 7.03125 27.255 7.36328 26.923 7.70508L15.5265 19.0723C14.8136 19.7754 14.1202 19.7754 13.4074 19.0723Z", fillAlpha = 0.85f)
        }
        return _sFFieldOfViewWide!!
    }

private var _sFFieldOfViewWide: ImageVector? = null
