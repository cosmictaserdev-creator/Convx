package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFChevronBackwardSquare (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFChevronBackwardSquare: ImageVector
    get() {
        if (_sFChevronBackwardSquare != null) {
            return _sFChevronBackwardSquare!!
        }
        _sFChevronBackwardSquare = sfIcon(
            name = "Dualtone.SFChevronBackwardSquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.425f)
            addSfPath("M13.9355 17.6758C14.2676 17.3535 14.2578 16.8262 13.9355 16.5137L8.63281 11.4648L13.9355 6.42578C14.2676 6.12305 14.2578 5.58594 13.9258 5.27344C13.6133 4.9707 13.1152 4.98047 12.7832 5.30273L7.40234 10.4199C6.78711 10.9961 6.78711 11.9629 7.40234 12.5293L12.7832 17.6465C13.0859 17.9492 13.6426 17.959 13.9355 17.6758Z", fillAlpha = 0.85f)
        }
        return _sFChevronBackwardSquare!!
    }

private var _sFChevronBackwardSquare: ImageVector? = null
