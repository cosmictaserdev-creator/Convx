package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPointerArrowIpadSquare (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFPointerArrowIpadSquare: ImageVector
    get() {
        if (_sFPointerArrowIpadSquare != null) {
            return _sFPointerArrowIpadSquare!!
        }
        _sFPointerArrowIpadSquare = sfIcon(
            name = "Dualtone.SFPointerArrowIpadSquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.425f)
            addSfPath("M12.0801 15.1074L16.7285 15.1074C17.7539 15.1074 18.1445 14.1699 17.5293 13.5449L8.85742 4.86328C8.16406 4.17969 7.10938 4.54102 7.10938 5.54688L7.10938 17.8613C7.10938 18.7402 8.05664 19.1211 8.78906 18.3984Z", fillAlpha = 0.85f)
        }
        return _sFPointerArrowIpadSquare!!
    }

private var _sFPointerArrowIpadSquare: ImageVector? = null
