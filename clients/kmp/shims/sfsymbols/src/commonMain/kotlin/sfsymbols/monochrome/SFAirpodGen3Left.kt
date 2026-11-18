package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFAirpodGen3Left (monochrome)
 * Viewport: 13.9648 x 21.6699
 */
public val SfSymbols.Monochrome.SFAirpodGen3Left: ImageVector
    get() {
        if (_sFAirpodGen3Left != null) {
            return _sFAirpodGen3Left!!
        }
        _sFAirpodGen3Left = sfIcon(
            name = "Monochrome.SFAirpodGen3Left",
            viewportWidth = 13.9648f,
            viewportHeight = 21.6699f
        ) {
            addSfPath("M5.2832 0.0878906C2.43164 0.107422 0 2.45117 0 5.61523C0 8.89648 4.69727 12.8809 8.13477 12.9004C10.6152 12.9199 13.6035 10.5176 13.6035 7.50977C13.6035 3.87695 9.0918 0.0585938 5.2832 0.0878906ZM9.29688 11.2109C8.62305 11.2109 8.59375 10.3516 9.24805 9.27734C9.90234 8.18359 10.9766 7.33398 11.6504 7.33398C12.3438 7.33398 12.373 8.18359 11.709 9.27734C11.0547 10.3516 9.99023 11.2109 9.29688 11.2109ZM5.84961 5.68359C5.48828 6.23047 4.75586 6.33789 4.20898 5.94727L2.4707 4.7168C1.93359 4.32617 1.81641 3.62305 2.22656 3.08594C2.58789 2.54883 3.32031 2.42188 3.83789 2.80273L5.57617 4.0625C6.10352 4.43359 6.2207 5.16602 5.84961 5.68359ZM4.64844 21.6699C5.39062 21.6699 5.84961 21.2891 5.84961 20.5859L5.84961 13.6328C4.45312 13.1738 3.07617 12.3242 1.91406 11.2695L1.91406 20.5859C1.91406 21.2891 2.36328 21.6699 3.10547 21.6699Z", fillAlpha = 0.85f)
        }
        return _sFAirpodGen3Left!!
    }

private var _sFAirpodGen3Left: ImageVector? = null
