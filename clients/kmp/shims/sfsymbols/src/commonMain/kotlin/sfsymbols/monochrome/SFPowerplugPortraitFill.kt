package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPowerplugPortraitFill (monochrome)
 * Viewport: 17.4902 x 28.4277
 */
public val SfSymbols.Monochrome.SFPowerplugPortraitFill: ImageVector
    get() {
        if (_sFPowerplugPortraitFill != null) {
            return _sFPowerplugPortraitFill!!
        }
        _sFPowerplugPortraitFill = sfIcon(
            name = "Monochrome.SFPowerplugPortraitFill",
            viewportWidth = 17.4902f,
            viewportHeight = 28.4277f
        ) {
            addSfPath("M6.91406 28.3984L10.2148 28.3984C11.4844 28.3984 12.1484 27.7246 12.1484 26.4355L12.1484 21.6699C12.1484 20.2344 12.832 19.4434 14.0527 18.5352C16.0645 17.0215 17.1289 14.7266 17.1289 12.3047L17.1289 8.53516C17.1289 7.28516 16.4648 6.63086 15.2148 6.61133L13.877 6.60156L13.877 1.47461C13.877 0.664062 13.2227 0 12.4023 0C11.582 0 10.918 0.664062 10.918 1.47461L10.918 6.60156L6.19141 6.60156L6.19141 1.47461C6.19141 0.664062 5.54688 0 4.73633 0C3.91602 0 3.25195 0.664062 3.25195 1.47461L3.25195 6.60156L1.88477 6.61133C0.634766 6.63086 0 7.28516 0 8.53516L0 12.3047C0 14.7266 1.06445 17.0215 3.08594 18.5352C4.31641 19.4434 4.98047 20.2344 4.98047 21.6699L4.98047 26.4355C4.98047 27.7246 5.625 28.3984 6.91406 28.3984Z", fillAlpha = 0.85f)
        }
        return _sFPowerplugPortraitFill!!
    }

private var _sFPowerplugPortraitFill: ImageVector? = null
