package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFKSquare (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFKSquare: ImageVector
    get() {
        if (_sFKSquare != null) {
            return _sFKSquare!!
        }
        _sFKSquare = sfIcon(
            name = "Monochrome.SFKSquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.85f)
            addSfPath("M8.19336 17.2168C8.75 17.2168 9.0625 16.8555 9.0625 16.2598L9.0625 13.3594L10.6445 11.7383L14.4922 16.7773C14.7266 17.0801 14.9512 17.207 15.2637 17.207C15.752 17.207 16.0938 16.875 16.0938 16.3965C16.0938 16.1719 15.9961 15.9375 15.8398 15.7227L11.9043 10.6152L15.5273 6.9043C15.7227 6.70898 15.8008 6.52344 15.8008 6.28906C15.8008 5.87891 15.4492 5.55664 15.0098 5.55664C14.7266 5.55664 14.541 5.64453 14.2969 5.89844L9.13086 11.1426L9.0625 11.1426L9.0625 6.51367C9.0625 5.9082 8.75 5.55664 8.19336 5.55664C7.66602 5.55664 7.34375 5.9082 7.34375 6.51367L7.34375 16.2598C7.34375 16.8555 7.66602 17.2168 8.19336 17.2168Z", fillAlpha = 0.85f)
        }
        return _sFKSquare!!
    }

private var _sFKSquare: ImageVector? = null
