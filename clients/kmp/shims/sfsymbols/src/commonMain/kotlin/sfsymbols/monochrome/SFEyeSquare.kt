package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFEyeSquare (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFEyeSquare: ImageVector
    get() {
        if (_sFEyeSquare != null) {
            return _sFEyeSquare!!
        }
        _sFEyeSquare = sfIcon(
            name = "Monochrome.SFEyeSquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.85f)
            addSfPath("M11.4844 16.8555C16.4941 16.8555 20.0195 12.7441 20.0195 11.4746C20.0195 10.2051 16.4746 6.09375 11.4844 6.09375C6.46484 6.09375 2.94922 10.2051 2.94922 11.4746C2.94922 12.7441 6.51367 16.8555 11.4844 16.8555ZM11.4844 15C9.52148 15 7.94922 13.3887 7.93945 11.4746C7.93945 9.51172 9.52148 7.94922 11.4844 7.94922C13.4277 7.94922 15.0195 9.51172 15.0195 11.4746C15.0195 13.3887 13.4277 15 11.4844 15ZM11.5039 12.959C12.2949 12.959 12.9785 12.2656 12.9785 11.4746C12.9785 10.6738 12.2949 9.99023 11.5039 9.99023C10.6836 9.99023 9.99023 10.6738 9.99023 11.4746C9.99023 12.2656 10.6836 12.959 11.5039 12.959Z", fillAlpha = 0.85f)
        }
        return _sFEyeSquare!!
    }

private var _sFEyeSquare: ImageVector? = null
