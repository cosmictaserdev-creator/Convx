package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPlaySquare (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFPlaySquare: ImageVector
    get() {
        if (_sFPlaySquare != null) {
            return _sFPlaySquare!!
        }
        _sFPlaySquare = sfIcon(
            name = "Dualtone.SFPlaySquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.425f)
            addSfPath("M9.11133 16.3965L16.2988 12.1289C16.8066 11.8262 16.7969 11.1426 16.2988 10.8398L9.11133 6.55273C8.60352 6.25 7.92969 6.49414 7.92969 7.06055L7.92969 15.8984C7.92969 16.4648 8.56445 16.7285 9.11133 16.3965Z", fillAlpha = 0.85f)
        }
        return _sFPlaySquare!!
    }

private var _sFPlaySquare: ImageVector? = null
