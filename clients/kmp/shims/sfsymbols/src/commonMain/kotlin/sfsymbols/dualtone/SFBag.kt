package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBag (dualtone)
 * Viewport: 22.4512 x 25.0293
 */
public val SfSymbols.Dualtone.SFBag: ImageVector
    get() {
        if (_sFBag != null) {
            return _sFBag!!
        }
        _sFBag = sfIcon(
            name = "Dualtone.SFBag",
            viewportWidth = 22.4512f,
            viewportHeight = 25.0293f
        ) {
            addSfPath("M3.79883 25.0195L18.584 25.0195C20.8203 25.0195 22.0898 23.7402 22.0898 21.25L22.0898 8.10547C22.0898 5.625 20.8105 4.3457 18.291 4.3457L3.79883 4.3457C1.2793 4.3457 0 5.61523 0 8.10547L0 21.25C0 23.75 1.2793 25.0195 3.79883 25.0195ZM3.83789 23.2812C2.4707 23.2812 1.72852 22.5586 1.72852 21.1621L1.72852 8.20312C1.72852 6.79688 2.4707 6.07422 3.83789 6.07422L18.252 6.07422C19.5898 6.07422 20.3613 6.79688 20.3613 8.20312L20.3613 21.1621C20.3613 22.5586 19.5898 23.2812 18.5547 23.2812ZM6.23047 4.83398L7.95898 4.84375C7.95898 2.98828 9.23828 1.64062 11.0449 1.64062C12.8516 1.64062 14.1309 2.98828 14.1309 4.84375L15.8594 4.83398C15.8594 2.12891 13.7695 0 11.0449 0C8.32031 0 6.23047 2.12891 6.23047 4.83398Z", fillAlpha = 0.85f)
        }
        return _sFBag!!
    }

private var _sFBag: ImageVector? = null
