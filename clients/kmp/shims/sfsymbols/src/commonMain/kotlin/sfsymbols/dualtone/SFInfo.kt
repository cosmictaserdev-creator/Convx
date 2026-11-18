package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFInfo (dualtone)
 * Viewport: 12.4219 x 25.8301
 */
public val SfSymbols.Dualtone.SFInfo: ImageVector
    get() {
        if (_sFInfo != null) {
            return _sFInfo!!
        }
        _sFInfo = sfIcon(
            name = "Dualtone.SFInfo",
            viewportWidth = 12.4219f,
            viewportHeight = 25.8301f
        ) {
            addSfPath("M0.283203 23.3203C0.283203 23.7891 0.693359 24.1797 1.2207 24.1797L11.4941 24.1797C12.0117 24.1797 12.4219 23.7891 12.4219 23.3203C12.4219 22.8418 12.0117 22.4707 11.4941 22.4707L7.73438 22.4707L7.73438 9.32617C7.73438 8.78906 7.32422 8.36914 6.80664 8.36914L1.76758 8.36914C1.24023 8.36914 0.820312 8.74023 0.820312 9.19922C0.820312 9.6875 1.24023 10.0684 1.76758 10.0684L5.76172 10.0684L5.76172 22.4707L1.2207 22.4707C0.693359 22.4707 0.283203 22.8418 0.283203 23.3203ZM6.14258 3.65234C7.1582 3.65234 7.96875 2.8418 7.96875 1.82617C7.96875 0.820312 7.1582 0 6.14258 0C5.13672 0 4.31641 0.820312 4.31641 1.82617C4.31641 2.8418 5.13672 3.65234 6.14258 3.65234Z", fillAlpha = 0.85f)
        }
        return _sFInfo!!
    }

private var _sFInfo: ImageVector? = null
