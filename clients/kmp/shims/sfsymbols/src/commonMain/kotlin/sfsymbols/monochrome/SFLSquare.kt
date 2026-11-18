package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLSquare (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFLSquare: ImageVector
    get() {
        if (_sFLSquare != null) {
            return _sFLSquare!!
        }
        _sFLSquare = sfIcon(
            name = "Monochrome.SFLSquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.85f)
            addSfPath("M9.07227 17.0898L14.9316 17.0898C15.3516 17.0898 15.6543 16.8066 15.6543 16.3672C15.6543 15.918 15.3516 15.6543 14.9316 15.6543L9.95117 15.6543L9.95117 6.51367C9.95117 5.91797 9.63867 5.55664 9.07227 5.55664C8.51562 5.55664 8.22266 5.9375 8.22266 6.51367L8.22266 16.123C8.22266 16.6895 8.52539 17.0898 9.07227 17.0898Z", fillAlpha = 0.85f)
        }
        return _sFLSquare!!
    }

private var _sFLSquare: ImageVector? = null
