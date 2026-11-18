package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFZSquare (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFZSquare: ImageVector
    get() {
        if (_sFZSquare != null) {
            return _sFZSquare!!
        }
        _sFZSquare = sfIcon(
            name = "Dualtone.SFZSquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.425f)
            addSfPath("M7.87109 17.0898L15.2441 17.0898C15.6641 17.0898 15.9668 16.8066 15.9668 16.3672C15.9668 15.9277 15.6641 15.6543 15.2441 15.6543L9.28711 15.6543L9.28711 15.5566L15.3125 7.22656C15.5371 6.91406 15.5859 6.75781 15.5859 6.49414C15.5859 6.01562 15.2441 5.68359 14.7461 5.68359L7.76367 5.68359C7.34375 5.68359 7.03125 5.9668 7.03125 6.39648C7.03125 6.8457 7.34375 7.11914 7.76367 7.11914L13.3984 7.11914L13.3984 7.20703L7.36328 15.5371C7.10938 15.8887 7.05078 16.0547 7.05078 16.3086C7.05078 16.7676 7.38281 17.0898 7.87109 17.0898Z", fillAlpha = 0.85f)
        }
        return _sFZSquare!!
    }

private var _sFZSquare: ImageVector? = null
