package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFFlagSquare (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFFlagSquare: ImageVector
    get() {
        if (_sFFlagSquare != null) {
            return _sFFlagSquare!!
        }
        _sFFlagSquare = sfIcon(
            name = "Dualtone.SFFlagSquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.425f)
            addSfPath("M6.66016 18.2129C6.96289 18.2129 7.17773 17.9785 7.17773 17.6953L7.17773 13.8965C7.42188 13.7988 7.93945 13.6328 8.75 13.6328C11.0059 13.6328 12.4023 14.7559 14.6289 14.7559C15.5469 14.7559 15.9668 14.6582 16.4453 14.4336C16.9531 14.1992 17.1777 13.8672 17.1777 13.3594L17.1777 7.11914C17.1777 6.76758 16.9922 6.5332 16.5332 6.5332C16.1816 6.5332 15.6152 6.80664 14.5605 6.80664C12.3926 6.80664 10.9766 5.67383 8.68164 5.67383C7.73438 5.67383 7.31445 5.79102 6.875 5.98633C6.37695 6.20117 6.13281 6.57227 6.13281 7.06055L6.13281 17.6953C6.13281 17.9688 6.36719 18.2129 6.66016 18.2129Z", fillAlpha = 0.85f)
        }
        return _sFFlagSquare!!
    }

private var _sFFlagSquare: ImageVector? = null
