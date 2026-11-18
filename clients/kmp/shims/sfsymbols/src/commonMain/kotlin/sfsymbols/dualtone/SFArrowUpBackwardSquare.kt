package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowUpBackwardSquare (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFArrowUpBackwardSquare: ImageVector
    get() {
        if (_sFArrowUpBackwardSquare != null) {
            return _sFArrowUpBackwardSquare!!
        }
        _sFArrowUpBackwardSquare = sfIcon(
            name = "Dualtone.SFArrowUpBackwardSquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.425f)
            addSfPath("M8.78906 7.77344C7.97852 7.02148 6.98242 7.94922 7.77344 8.79883L9.74609 10.8984L14.9023 16.0449C15.0488 16.1914 15.2539 16.2891 15.5176 16.2891C15.9961 16.2891 16.3184 15.9766 16.3184 15.4883C16.3184 15.2637 16.2109 15.0586 16.0547 14.9023L10.8984 9.74609ZM8.22266 11.1523L7.94922 7.91992L10.9375 8.22266L13.7012 8.22266C14.2188 8.22266 14.5508 7.90039 14.5508 7.42188C14.5508 6.95312 14.2285 6.64062 13.7207 6.64062L7.5293 6.64062C6.98242 6.64062 6.64062 6.89453 6.64062 7.5293L6.64062 13.6816C6.64062 14.1895 6.95312 14.541 7.43164 14.541C7.91016 14.541 8.22266 14.2188 8.22266 13.7012Z", fillAlpha = 0.85f)
        }
        return _sFArrowUpBackwardSquare!!
    }

private var _sFArrowUpBackwardSquare: ImageVector? = null
