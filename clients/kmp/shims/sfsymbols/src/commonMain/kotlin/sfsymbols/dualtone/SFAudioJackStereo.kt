package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFAudioJackStereo (dualtone)
 * Viewport: 7.40234 x 28.3301
 */
public val SfSymbols.Dualtone.SFAudioJackStereo: ImageVector
    get() {
        if (_sFAudioJackStereo != null) {
            return _sFAudioJackStereo!!
        }
        _sFAudioJackStereo = sfIcon(
            name = "Dualtone.SFAudioJackStereo",
            viewportWidth = 7.40234f,
            viewportHeight = 28.3301f
        ) {
            addSfPath("M3.52539 23.6914C5.45898 23.6914 7.04102 22.1094 7.04102 20.1758L7.04102 14.5312C7.04102 14.1309 6.76758 13.8477 6.35742 13.8477L0.693359 13.8477C0.273438 13.8477 0 14.1309 0 14.5312L0 20.1758C0 22.1094 1.58203 23.6914 3.52539 23.6914ZM2.59766 28.3301L4.45312 28.3301L4.45312 22.9199L2.58789 22.9199ZM2.38281 12.4902L4.6582 12.4902L4.6582 6.88477L2.38281 6.88477ZM2.38281 5.625L4.6582 5.625L4.6582 3.59375L2.38281 3.59375ZM2.38281 2.33398L4.6582 2.33398L4.6582 0.703125C4.6582 0.283203 4.38477 0.00976562 3.97461 0.00976562L3.06641 0.00976562C2.65625 0.00976562 2.38281 0.283203 2.38281 0.703125Z", fillAlpha = 0.85f)
        }
        return _sFAudioJackStereo!!
    }

private var _sFAudioJackStereo: ImageVector? = null
