package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowtriangleUpSquare (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFArrowtriangleUpSquare: ImageVector
    get() {
        if (_sFArrowtriangleUpSquare != null) {
            return _sFArrowtriangleUpSquare!!
        }
        _sFArrowtriangleUpSquare = sfIcon(
            name = "Monochrome.SFArrowtriangleUpSquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.85f)
            addSfPath("M7.06055 15.3027L15.8984 15.3027C16.4648 15.3027 16.7285 14.6777 16.3965 14.1211L12.1387 6.93359C11.8262 6.41602 11.1426 6.43555 10.8398 6.93359L6.55273 14.1211C6.25 14.6387 6.49414 15.3027 7.06055 15.3027Z", fillAlpha = 0.85f)
        }
        return _sFArrowtriangleUpSquare!!
    }

private var _sFArrowtriangleUpSquare: ImageVector? = null
