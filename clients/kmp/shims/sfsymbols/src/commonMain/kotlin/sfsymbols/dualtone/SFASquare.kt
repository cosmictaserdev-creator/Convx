package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFASquare (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFASquare: ImageVector
    get() {
        if (_sFASquare != null) {
            return _sFASquare!!
        }
        _sFASquare = sfIcon(
            name = "Dualtone.SFASquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.425f)
            addSfPath("M7.34375 17.373C7.73438 17.373 8.00781 17.1973 8.17383 16.6797L9.16992 13.8477L13.7793 13.8477L14.7754 16.6797C14.9512 17.1875 15.2148 17.373 15.625 17.373C16.0938 17.373 16.416 17.0703 16.416 16.6309C16.416 16.4648 16.3867 16.3086 16.2988 16.0742L12.6758 6.35742C12.4609 5.80078 12.0508 5.51758 11.4648 5.51758C10.8887 5.51758 10.4883 5.80078 10.2734 6.35742L6.65039 16.0742C6.57227 16.3086 6.5332 16.4648 6.5332 16.6309C6.5332 17.0801 6.8457 17.373 7.34375 17.373ZM9.61914 12.5195L11.416 7.43164L11.5332 7.43164L13.3301 12.5195Z", fillAlpha = 0.85f)
        }
        return _sFASquare!!
    }

private var _sFASquare: ImageVector? = null
