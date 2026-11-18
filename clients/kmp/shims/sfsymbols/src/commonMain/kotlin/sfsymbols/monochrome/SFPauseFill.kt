package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPauseFill (monochrome)
 * Viewport: 15.1953 x 20.5566
 */
public val SfSymbols.Monochrome.SFPauseFill: ImageVector
    get() {
        if (_sFPauseFill != null) {
            return _sFPauseFill!!
        }
        _sFPauseFill = sfIcon(
            name = "Monochrome.SFPauseFill",
            viewportWidth = 15.1953f,
            viewportHeight = 20.5566f
        ) {
            addSfPath("M1.5625 20.5371L4.375 20.5371C5.41992 20.5371 5.9375 20.0195 5.9375 18.9746L5.9375 1.5625C5.9375 0.478516 5.41992 0 4.375 0L1.5625 0C0.517578 0 0 0.517578 0 1.5625L0 18.9746C0 20.0195 0.517578 20.5371 1.5625 20.5371ZM10.459 20.5371L13.2715 20.5371C14.3164 20.5371 14.834 20.0195 14.834 18.9746L14.834 1.5625C14.834 0.478516 14.3164 0 13.2715 0L10.459 0C9.41406 0 8.89648 0.517578 8.89648 1.5625L8.89648 18.9746C8.89648 20.0195 9.41406 20.5371 10.459 20.5371Z", fillAlpha = 0.85f)
        }
        return _sFPauseFill!!
    }

private var _sFPauseFill: ImageVector? = null
