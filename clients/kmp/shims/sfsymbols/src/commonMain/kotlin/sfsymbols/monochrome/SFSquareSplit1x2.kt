package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSquareSplit1x2 (monochrome)
 * Viewport: 23.3203 x 22.9785
 */
public val SfSymbols.Monochrome.SFSquareSplit1x2: ImageVector
    get() {
        if (_sFSquareSplit1x2 != null) {
            return _sFSquareSplit1x2!!
        }
        _sFSquareSplit1x2 = sfIcon(
            name = "Monochrome.SFSquareSplit1x2",
            viewportWidth = 23.3203f,
            viewportHeight = 22.9785f
        ) {
            addSfPath("M1.16211 10.6348L1.16211 12.3633L21.8359 12.3633L21.8359 10.6348ZM0 3.82812L0 19.1797C0 21.6992 1.2793 22.9785 3.75977 22.9785L19.1895 22.9785C21.6797 22.9785 22.959 21.6992 22.959 19.1797L22.959 3.82812C22.959 1.30859 21.6895 0.0195312 19.1895 0.0195312L3.75977 0.0195312C1.25977 0.0195312 0 1.30859 0 3.82812ZM1.72852 3.85742C1.72852 2.49023 2.45117 1.74805 3.85742 1.74805L19.1016 1.74805C20.5078 1.74805 21.2305 2.49023 21.2305 3.85742L21.2305 19.1406C21.2305 20.4785 20.5078 21.25 19.1016 21.25L3.85742 21.25C2.45117 21.25 1.72852 20.4785 1.72852 19.1406Z", fillAlpha = 0.85f)
        }
        return _sFSquareSplit1x2!!
    }

private var _sFSquareSplit1x2: ImageVector? = null
