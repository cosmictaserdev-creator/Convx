package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMosaicFill (monochrome)
 * Viewport: 23.3203 x 22.9785
 */
public val SfSymbols.Monochrome.SFMosaicFill: ImageVector
    get() {
        if (_sFMosaicFill != null) {
            return _sFMosaicFill!!
        }
        _sFMosaicFill = sfIcon(
            name = "Monochrome.SFMosaicFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.9785f
        ) {
            addSfPath("M0 16.4648L0 14.7363L14.707 14.7363L14.707 0.0292969L16.4355 0.0292969L16.4355 6.54297L22.959 6.54297L22.959 8.27148L16.4355 8.27148L16.4355 14.7363L22.959 14.7363L22.959 16.4648L16.4355 16.4648L16.4355 22.9785L14.707 22.9785L14.707 16.4648L8.25195 16.4648L8.25195 22.9785L6.51367 22.9785L6.51367 16.4648ZM3.79883 22.9688L19.1504 22.9688C21.6797 22.9688 22.959 21.6895 22.959 19.209L22.959 3.7793C22.959 1.28906 21.6797 0.00976562 19.1504 0.00976562L3.79883 0.00976562C1.2793 0.00976562 0 1.2793 0 3.7793L0 19.209C0 21.709 1.2793 22.9688 3.79883 22.9688Z", fillAlpha = 0.85f)
        }
        return _sFMosaicFill!!
    }

private var _sFMosaicFill: ImageVector? = null
