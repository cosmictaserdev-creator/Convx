package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFOSquare (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFOSquare: ImageVector
    get() {
        if (_sFOSquare != null) {
            return _sFOSquare!!
        }
        _sFOSquare = sfIcon(
            name = "Monochrome.SFOSquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.85f)
            addSfPath("M11.4844 17.3438C14.6582 17.3438 16.8359 14.9121 16.8359 11.3867C16.8359 7.85156 14.6582 5.41992 11.4844 5.41992C8.31055 5.41992 6.13281 7.85156 6.13281 11.3867C6.13281 14.9121 8.31055 17.3438 11.4844 17.3438ZM11.4844 15.957C9.30664 15.957 7.85156 14.1406 7.85156 11.3867C7.85156 8.63281 9.30664 6.80664 11.4844 6.80664C13.6621 6.80664 15.1172 8.63281 15.1172 11.3867C15.1172 14.1406 13.6621 15.957 11.4844 15.957Z", fillAlpha = 0.85f)
        }
        return _sFOSquare!!
    }

private var _sFOSquare: ImageVector? = null
