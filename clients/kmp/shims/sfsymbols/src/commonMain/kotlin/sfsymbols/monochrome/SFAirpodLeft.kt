package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFAirpodLeft (monochrome)
 * Viewport: 11.0454 x 24.0723
 */
public val SfSymbols.Monochrome.SFAirpodLeft: ImageVector
    get() {
        if (_sFAirpodLeft != null) {
            return _sFAirpodLeft!!
        }
        _sFAirpodLeft = sfIcon(
            name = "Monochrome.SFAirpodLeft",
            viewportWidth = 11.0454f,
            viewportHeight = 24.0723f
        ) {
            addSfPath("M5.28346 9.93164C7.81276 9.89258 9.49245 8.41797 10.3128 7.14844C10.5667 6.76758 10.6839 6.35742 10.6839 5.87891L10.6839 4.04297C10.6839 3.58398 10.5764 3.16406 10.3128 2.77344C9.47292 1.51367 7.81276 0 5.28346 0C2.41237 0 0.0295578 2.23633 0.000260972 4.95117C-0.0290359 7.69531 2.41237 9.95117 5.28346 9.93164ZM8.47682 6.875C8.16432 6.875 7.92018 6.63086 7.92018 6.30859L7.92018 3.60352C7.92018 3.29102 8.16432 3.05664 8.47682 3.05664C8.79909 3.05664 9.03346 3.29102 9.03346 3.60352L9.03346 6.30859C9.03346 6.63086 8.79909 6.875 8.47682 6.875ZM4.5901 20.0488L4.5901 11.0547C3.3401 10.9375 2.16823 10.459 1.22096 9.73633L1.22096 20.0488ZM3.43776 24.0332C4.14089 24.0332 4.5901 23.6719 4.5901 22.998L4.5901 21.25L1.22096 21.25L1.22096 22.998C1.22096 23.6719 1.66042 24.0332 2.38307 24.0332Z", fillAlpha = 0.85f)
        }
        return _sFAirpodLeft!!
    }

private var _sFAirpodLeft: ImageVector? = null
