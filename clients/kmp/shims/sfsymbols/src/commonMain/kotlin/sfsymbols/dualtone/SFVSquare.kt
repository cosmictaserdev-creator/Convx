package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFVSquare (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFVSquare: ImageVector
    get() {
        if (_sFVSquare != null) {
            return _sFVSquare!!
        }
        _sFVSquare = sfIcon(
            name = "Dualtone.SFVSquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.425f)
            addSfPath("M11.4941 17.3145C12.1289 17.3145 12.5293 16.9824 12.7832 16.2988L16.3965 6.8457C16.4648 6.66016 16.4844 6.5332 16.4844 6.36719C16.4844 5.88867 16.123 5.56641 15.6152 5.56641C15.1465 5.56641 14.873 5.78125 14.6973 6.29883L11.5234 15.2539L11.4453 15.2539L8.27148 6.29883C8.08594 5.77148 7.82227 5.56641 7.35352 5.56641C6.8457 5.56641 6.48438 5.89844 6.48438 6.36719C6.48438 6.5332 6.50391 6.66016 6.57227 6.8457L10.1953 16.3086C10.4688 17.0312 10.8301 17.3145 11.4941 17.3145Z", fillAlpha = 0.85f)
        }
        return _sFVSquare!!
    }

private var _sFVSquare: ImageVector? = null
