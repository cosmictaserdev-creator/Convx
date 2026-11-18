package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFAudioJackMono (monochrome)
 * Viewport: 7.40234 x 28.3301
 */
public val SfSymbols.Monochrome.SFAudioJackMono: ImageVector
    get() {
        if (_sFAudioJackMono != null) {
            return _sFAudioJackMono!!
        }
        _sFAudioJackMono = sfIcon(
            name = "Monochrome.SFAudioJackMono",
            viewportWidth = 7.40234f,
            viewportHeight = 28.3301f
        ) {
            addSfPath("M3.52539 22.8125C5.45898 22.8125 7.04102 21.2305 7.04102 19.2969L7.04102 12.2363C7.04102 11.8262 6.76758 11.5527 6.35742 11.5527L0.693359 11.5527C0.273438 11.5527 0 11.8262 0 12.2363L0 19.2969C0 21.2305 1.58203 22.8125 3.52539 22.8125ZM2.59766 28.3301L4.45312 28.3301L4.45312 22.041L2.58789 22.041ZM2.38281 10.1953L4.6582 10.1953L4.6582 3.58398L2.38281 3.58398ZM2.38281 2.32422L4.6582 2.32422L4.6582 0.703125C4.6582 0.283203 4.38477 0.00976562 3.97461 0.00976562L3.06641 0.00976562C2.65625 0.00976562 2.38281 0.283203 2.38281 0.703125Z", fillAlpha = 0.85f)
        }
        return _sFAudioJackMono!!
    }

private var _sFAudioJackMono: ImageVector? = null
