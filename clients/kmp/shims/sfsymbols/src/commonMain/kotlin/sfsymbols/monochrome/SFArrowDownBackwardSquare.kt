package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowDownBackwardSquare (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFArrowDownBackwardSquare: ImageVector
    get() {
        if (_sFArrowDownBackwardSquare != null) {
            return _sFArrowDownBackwardSquare!!
        }
        _sFArrowDownBackwardSquare = sfIcon(
            name = "Monochrome.SFArrowDownBackwardSquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.85f)
            addSfPath("M8.78906 15.1855L10.8984 13.2031L16.0547 8.04688C16.2109 7.89062 16.3184 7.68555 16.3184 7.46094C16.3184 6.97266 15.9961 6.66016 15.5176 6.66016C15.2539 6.66016 15.0488 6.75781 14.9023 6.91406L9.74609 12.0605L7.77344 14.1602C6.98242 15 7.97852 15.9375 8.78906 15.1855ZM8.22266 11.8066L8.22266 9.24805C8.22266 8.73047 7.91016 8.4082 7.43164 8.4082C6.95312 8.4082 6.64062 8.75977 6.64062 9.26758L6.64062 15.4297C6.64062 16.0547 6.98242 16.3086 7.5293 16.3086L13.7207 16.3086C14.2285 16.3086 14.5508 16.0059 14.5508 15.5273C14.5508 15.0488 14.2188 14.7363 13.7012 14.7363L10.9375 14.7363L7.94922 15.0293Z", fillAlpha = 0.85f)
        }
        return _sFArrowDownBackwardSquare!!
    }

private var _sFArrowDownBackwardSquare: ImageVector? = null
