package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTSquare (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFTSquare: ImageVector
    get() {
        if (_sFTSquare != null) {
            return _sFTSquare!!
        }
        _sFTSquare = sfIcon(
            name = "Monochrome.SFTSquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.85f)
            addSfPath("M11.4355 17.2559C12.002 17.2559 12.3145 16.8848 12.3145 16.2988L12.3145 7.1582L15.5566 7.1582C15.9766 7.1582 16.2793 6.875 16.2793 6.43555C16.2793 5.98633 15.9766 5.72266 15.5566 5.72266L7.40234 5.72266C6.98242 5.72266 6.66992 5.98633 6.66992 6.43555C6.66992 6.875 6.98242 7.1582 7.40234 7.1582L10.5859 7.1582L10.5859 16.2988C10.5859 16.8652 10.8887 17.2559 11.4355 17.2559Z", fillAlpha = 0.85f)
        }
        return _sFTSquare!!
    }

private var _sFTSquare: ImageVector? = null
