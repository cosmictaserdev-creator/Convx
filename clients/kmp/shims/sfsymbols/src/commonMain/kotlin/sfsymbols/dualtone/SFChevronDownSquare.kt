package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFChevronDownSquare (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFChevronDownSquare: ImageVector
    get() {
        if (_sFChevronDownSquare != null) {
            return _sFChevronDownSquare!!
        }
        _sFChevronDownSquare = sfIcon(
            name = "Dualtone.SFChevronDownSquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.425f)
            addSfPath("M12.5586 15.5664L17.6758 10.1953C17.998 9.84375 18.0176 9.3457 17.7051 9.0332C17.373 8.70117 16.8457 8.68164 16.5332 9.02344L11.4941 14.3164L6.44531 9.02344C6.13281 8.68164 5.5957 8.69141 5.2832 9.0332C4.98047 9.35547 4.99023 9.84375 5.3125 10.1953L10.4297 15.5664C11.1035 16.2891 11.8848 16.2793 12.5586 15.5664Z", fillAlpha = 0.85f)
        }
        return _sFChevronDownSquare!!
    }

private var _sFChevronDownSquare: ImageVector? = null
