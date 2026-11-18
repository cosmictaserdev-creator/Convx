package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFAirplane (monochrome)
 * Viewport: 30.6445 x 25.0098
 */
public val SfSymbols.Monochrome.SFAirplane: ImageVector
    get() {
        if (_sFAirplane != null) {
            return _sFAirplane!!
        }
        _sFAirplane = sfIcon(
            name = "Monochrome.SFAirplane",
            viewportWidth = 30.6445f,
            viewportHeight = 25.0098f
        ) {
            addSfPath("M30.6445 12.5C30.6348 11.0645 28.6719 10.0195 26.3281 10.0195L21.3086 10.0195C20.625 10.0195 20.3711 9.90234 19.9512 9.44336L11.709 0.439453C11.4551 0.15625 11.1328 0 10.7812 0L9.3457 0C9.02344 0 8.84766 0.283203 9.00391 0.625L13.2422 10.0195L6.875 10.7227L4.6582 6.65039C4.48242 6.34766 4.23828 6.2207 3.83789 6.2207L3.29102 6.2207C2.96875 6.2207 2.76367 6.40625 2.76367 6.73828L2.76367 18.2617C2.76367 18.584 2.96875 18.7793 3.29102 18.7793L3.83789 18.7793C4.23828 18.7793 4.48242 18.6426 4.6582 18.3496L6.875 14.2773L13.2422 14.9805L9.00391 24.375C8.84766 24.707 9.02344 24.9902 9.3457 24.9902L10.7812 24.9902C11.1328 24.9902 11.4551 24.834 11.709 24.5508L19.9512 15.5469C20.3711 15.0879 20.625 14.9805 21.3086 14.9805L26.3281 14.9805C28.6719 14.9805 30.6348 13.9258 30.6445 12.5Z", fillAlpha = 0.85f)
        }
        return _sFAirplane!!
    }

private var _sFAirplane: ImageVector? = null
