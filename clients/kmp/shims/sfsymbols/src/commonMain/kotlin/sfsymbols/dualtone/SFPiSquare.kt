package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPiSquare (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFPiSquare: ImageVector
    get() {
        if (_sFPiSquare != null) {
            return _sFPiSquare!!
        }
        _sFPiSquare = sfIcon(
            name = "Dualtone.SFPiSquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.425f)
            addSfPath("M8.31055 17.3535C8.7793 17.3535 9.11133 17.0508 9.11133 16.5918L9.11133 7.37305L13.7012 7.37305L13.7012 14.7559C13.7012 16.4258 14.4238 17.3145 15.9863 17.3145C16.8945 17.3145 17.4023 17.0215 17.4023 16.4746C17.4023 16.0352 17.1484 15.8008 16.6406 15.8008C16.4941 15.8008 16.3379 15.8203 16.1816 15.8203C15.5957 15.8203 15.3027 15.498 15.3027 14.707L15.3027 7.37305L17.002 7.37305C17.4121 7.37305 17.7246 7.08008 17.7246 6.63086C17.7246 6.18164 17.4219 5.88867 17.002 5.88867L5.85938 5.88867C5.42969 5.88867 5.11719 6.18164 5.11719 6.63086C5.11719 7.08008 5.42969 7.37305 5.85938 7.37305L7.5 7.37305L7.5 16.5918C7.5 17.0508 7.83203 17.3535 8.31055 17.3535Z", fillAlpha = 0.85f)
        }
        return _sFPiSquare!!
    }

private var _sFPiSquare: ImageVector? = null
