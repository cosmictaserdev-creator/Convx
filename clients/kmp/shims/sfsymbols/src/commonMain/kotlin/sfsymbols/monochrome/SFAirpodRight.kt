package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFAirpodRight (monochrome)
 * Viewport: 11.0449 x 24.0723
 */
public val SfSymbols.Monochrome.SFAirpodRight: ImageVector
    get() {
        if (_sFAirpodRight != null) {
            return _sFAirpodRight!!
        }
        _sFAirpodRight = sfIcon(
            name = "Monochrome.SFAirpodRight",
            viewportWidth = 11.0449f,
            viewportHeight = 24.0723f
        ) {
            addSfPath("M5.39062 9.93164C8.27148 9.95117 10.7129 7.69531 10.6836 4.95117C10.6543 2.23633 8.27148 0 5.39062 0C2.87109 0 1.21094 1.51367 0.361328 2.77344C0.107422 3.16406 0 3.58398 0 4.04297L0 5.87891C0 6.35742 0.117188 6.76758 0.361328 7.14844C1.19141 8.41797 2.87109 9.89258 5.39062 9.93164ZM2.19727 6.875C1.88477 6.875 1.64062 6.63086 1.64062 6.30859L1.64062 3.60352C1.64062 3.29102 1.88477 3.05664 2.19727 3.05664C2.51953 3.05664 2.76367 3.29102 2.76367 3.60352L2.76367 6.30859C2.76367 6.63086 2.51953 6.875 2.19727 6.875ZM6.08398 20.0488L9.45312 20.0488L9.45312 9.73633C8.51562 10.459 7.34375 10.9375 6.08398 11.0547ZM7.23633 24.0332L8.30078 24.0332C9.02344 24.0332 9.45312 23.6719 9.45312 22.998L9.45312 21.25L6.08398 21.25L6.08398 22.998C6.08398 23.6719 6.5332 24.0332 7.23633 24.0332Z", fillAlpha = 0.85f)
        }
        return _sFAirpodRight!!
    }

private var _sFAirpodRight: ImageVector? = null
