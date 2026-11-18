package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPillFill (monochrome)
 * Viewport: 24.7726 x 24.4508
 */
public val SfSymbols.Monochrome.SFPillFill: ImageVector
    get() {
        if (_sFPillFill != null) {
            return _sFPillFill!!
        }
        _sFPillFill = sfIcon(
            name = "Monochrome.SFPillFill",
            viewportWidth = 24.7726f,
            viewportHeight = 24.4508f
        ) {
            addSfPath("M7.23491 8.38262L2.21538 13.4119C-0.665481 16.2928-0.694778 19.9354 1.88335 22.5428C4.47124 25.1209 8.11382 25.0916 11.0044 22.201L16.024 17.1717Z", fillAlpha = 0.85f)
            addSfPath("M22.5279 1.87871C19.94-0.699412 16.2876-0.66035 13.4068 2.22051L8.38725 7.24004L17.1763 16.0291L22.1958 11.0096C25.0865 8.11895 25.1158 4.47637 22.5279 1.87871Z", fillAlpha = 0.85f)
        }
        return _sFPillFill!!
    }

private var _sFPillFill: ImageVector? = null
