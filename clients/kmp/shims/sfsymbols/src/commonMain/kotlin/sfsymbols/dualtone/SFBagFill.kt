package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBagFill (dualtone)
 * Viewport: 22.4512 x 25.0293
 */
public val SfSymbols.Dualtone.SFBagFill: ImageVector
    get() {
        if (_sFBagFill != null) {
            return _sFBagFill!!
        }
        _sFBagFill = sfIcon(
            name = "Dualtone.SFBagFill",
            viewportWidth = 22.4512f,
            viewportHeight = 25.0293f
        ) {
            addSfPath("M3.79883 25.0195L18.584 25.0195C20.8203 25.0195 22.0898 23.7402 22.0898 21.25L22.0898 8.10547C22.0898 5.625 20.8105 4.3457 18.291 4.3457L3.79883 4.3457C1.2793 4.3457 0 5.61523 0 8.10547L0 21.25C0 23.75 1.2793 25.0195 3.79883 25.0195ZM6.23047 4.83398L7.95898 4.84375C7.95898 2.98828 9.23828 1.64062 11.0449 1.64062C12.8516 1.64062 14.1309 2.98828 14.1309 4.84375L15.8594 4.83398C15.8594 2.12891 13.7695 0 11.0449 0C8.32031 0 6.23047 2.12891 6.23047 4.83398Z", fillAlpha = 0.85f)
        }
        return _sFBagFill!!
    }

private var _sFBagFill: ImageVector? = null
