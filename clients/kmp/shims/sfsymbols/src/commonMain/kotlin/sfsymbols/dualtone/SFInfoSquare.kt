package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFInfoSquare (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFInfoSquare: ImageVector
    get() {
        if (_sFInfoSquare != null) {
            return _sFInfoSquare!!
        }
        _sFInfoSquare = sfIcon(
            name = "Dualtone.SFInfoSquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.425f)
            addSfPath("M9.25781 18.4668L14.4043 18.4668C14.8535 18.4668 15.1953 18.1348 15.1953 17.6953C15.1953 17.2656 14.8535 16.9238 14.4043 16.9238L12.6855 16.9238L12.6855 10.0977C12.6855 9.53125 12.3926 9.14062 11.8457 9.14062L9.43359 9.14062C8.99414 9.14062 8.64258 9.47266 8.64258 9.90234C8.64258 10.3418 8.99414 10.6738 9.43359 10.6738L10.9766 10.6738L10.9766 16.9238L9.25781 16.9238C8.80859 16.9238 8.4668 17.2656 8.4668 17.6953C8.4668 18.1348 8.80859 18.4668 9.25781 18.4668ZM11.4062 6.91406C12.2363 6.91406 12.8809 6.25 12.8809 5.43945C12.8809 4.60938 12.2363 3.94531 11.4062 3.94531C10.5859 3.94531 9.93164 4.60938 9.93164 5.43945C9.93164 6.25 10.5859 6.91406 11.4062 6.91406Z", fillAlpha = 0.85f)
        }
        return _sFInfoSquare!!
    }

private var _sFInfoSquare: ImageVector? = null
