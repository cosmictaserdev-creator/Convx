package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBoltSquare (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFBoltSquare: ImageVector
    get() {
        if (_sFBoltSquare != null) {
            return _sFBoltSquare!!
        }
        _sFBoltSquare = sfIcon(
            name = "Dualtone.SFBoltSquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.425f)
            addSfPath("M5.92773 12.3828C5.92773 12.6758 6.15234 12.8809 6.45508 12.8809L10.8008 12.8809L8.47656 19.1504C8.18359 19.9023 8.97461 20.3027 9.47266 19.707L16.5039 10.8789C16.6309 10.7227 16.6992 10.5566 16.6992 10.4102C16.6992 10.1172 16.4648 9.92188 16.1719 9.92188L11.8262 9.92188L14.1504 3.65234C14.4336 2.90039 13.6426 2.49023 13.1543 3.0957L6.12305 11.9141C5.99609 12.0801 5.92773 12.2461 5.92773 12.3828Z", fillAlpha = 0.85f)
        }
        return _sFBoltSquare!!
    }

private var _sFBoltSquare: ImageVector? = null
