package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFYSquare (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFYSquare: ImageVector
    get() {
        if (_sFYSquare != null) {
            return _sFYSquare!!
        }
        _sFYSquare = sfIcon(
            name = "Dualtone.SFYSquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.425f)
            addSfPath("M11.4746 17.2168C12.041 17.2168 12.3535 16.8457 12.3535 16.2598L12.3535 12.666L15.9961 6.81641C16.0938 6.66016 16.1426 6.48438 16.1426 6.31836C16.1426 5.87891 15.8203 5.55664 15.3613 5.55664C14.9902 5.55664 14.7852 5.69336 14.5605 6.08398L11.5332 11.0254L11.4551 11.0254L8.42773 6.08398C8.20312 5.68359 7.98828 5.55664 7.60742 5.55664C7.1582 5.55664 6.81641 5.88867 6.81641 6.29883C6.81641 6.47461 6.86523 6.64062 6.96289 6.81641L10.625 12.6855L10.625 16.2598C10.625 16.8262 10.9277 17.2168 11.4746 17.2168Z", fillAlpha = 0.85f)
        }
        return _sFYSquare!!
    }

private var _sFYSquare: ImageVector? = null
