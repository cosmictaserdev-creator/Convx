package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPSquare (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFPSquare: ImageVector
    get() {
        if (_sFPSquare != null) {
            return _sFPSquare!!
        }
        _sFPSquare = sfIcon(
            name = "Monochrome.SFPSquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.85f)
            addSfPath("M8.76953 17.2168C9.33594 17.2168 9.64844 16.8457 9.64844 16.2598L9.64844 13.2031L12.2754 13.2031C14.6289 13.2031 16.2207 11.6895 16.2207 9.45312C16.2207 7.22656 14.6094 5.68359 12.2852 5.68359L8.75977 5.68359C8.21289 5.68359 7.91992 6.07422 7.91992 6.65039L7.91992 16.2598C7.91992 16.8262 8.22266 17.2168 8.76953 17.2168ZM9.64844 11.8555L9.64844 7.03125L12.0605 7.03125C13.5449 7.03125 14.5117 7.97852 14.5117 9.45312C14.5117 10.9082 13.5547 11.8555 12.0605 11.8555Z", fillAlpha = 0.85f)
        }
        return _sFPSquare!!
    }

private var _sFPSquare: ImageVector? = null
