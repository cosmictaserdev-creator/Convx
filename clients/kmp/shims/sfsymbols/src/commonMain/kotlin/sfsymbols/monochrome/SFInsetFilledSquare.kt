package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFInsetFilledSquare (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFInsetFilledSquare: ImageVector
    get() {
        if (_sFInsetFilledSquare != null) {
            return _sFInsetFilledSquare!!
        }
        _sFInsetFilledSquare = sfIcon(
            name = "Monochrome.SFInsetFilledSquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.85f)
            addSfPath("M4.67773 19.6582L18.291 19.6582C19.2285 19.6582 19.6777 19.209 19.6777 18.2617L19.6777 4.6875C19.6777 3.74023 19.2285 3.28125 18.291 3.28125L4.67773 3.28125C3.74023 3.28125 3.29102 3.74023 3.29102 4.6875L3.29102 18.2617C3.29102 19.209 3.74023 19.6582 4.67773 19.6582Z", fillAlpha = 0.85f)
        }
        return _sFInsetFilledSquare!!
    }

private var _sFInsetFilledSquare: ImageVector? = null
