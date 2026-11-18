package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSquareSplit2x2Fill (monochrome)
 * Viewport: 23.3203 x 22.9785
 */
public val SfSymbols.Monochrome.SFSquareSplit2x2Fill: ImageVector
    get() {
        if (_sFSquareSplit2x2Fill != null) {
            return _sFSquareSplit2x2Fill!!
        }
        _sFSquareSplit2x2Fill = sfIcon(
            name = "Monochrome.SFSquareSplit2x2Fill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.9785f
        ) {
            addSfPath("M0 12.373L0 10.6348L10.6152 10.6348L10.6152 0.0292969L12.3438 0.0292969L12.3438 10.6348L22.959 10.6348L22.959 12.373L12.3438 12.373L12.3438 22.9785L10.6152 22.9785L10.6152 12.373ZM3.79883 22.9688L19.1504 22.9688C21.6797 22.9688 22.959 21.6895 22.959 19.209L22.959 3.7793C22.959 1.28906 21.6797 0.00976562 19.1504 0.00976562L3.79883 0.00976562C1.2793 0.00976562 0 1.2793 0 3.7793L0 19.209C0 21.709 1.2793 22.9688 3.79883 22.9688Z", fillAlpha = 0.85f)
        }
        return _sFSquareSplit2x2Fill!!
    }

private var _sFSquareSplit2x2Fill: ImageVector? = null
