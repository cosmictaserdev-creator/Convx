package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowUturnDown (dualtone)
 * Viewport: 23.5352 x 23.9355
 */
public val SfSymbols.Dualtone.SFArrowUturnDown: ImageVector
    get() {
        if (_sFArrowUturnDown != null) {
            return _sFArrowUturnDown!!
        }
        _sFArrowUturnDown = sfIcon(
            name = "Dualtone.SFArrowUturnDown",
            viewportWidth = 23.5352f,
            viewportHeight = 23.9355f
        ) {
            addSfPath("M8.38867 23.8086C8.65234 23.8086 8.89648 23.7109 9.10156 23.4961L16.4844 16.0156C16.6699 15.8301 16.7773 15.5566 16.7773 15.3223C16.7773 14.7559 16.3867 14.3848 15.8398 14.3848C15.5664 14.3848 15.3516 14.4727 15.1855 14.6387L11.3965 18.4961L8.38867 21.9238L5.38086 18.4961L1.5918 14.6387C1.42578 14.4727 1.21094 14.3848 0.9375 14.3848C0.390625 14.3848 0 14.7559 0 15.3223C0 15.5566 0.107422 15.8301 0.292969 16.0156L7.67578 23.4961C7.88086 23.7109 8.125 23.8086 8.38867 23.8086ZM22.2266 12.7539C22.7441 12.7539 23.1738 12.3633 23.1738 11.7871L23.1738 8.52539C23.1738 3.13477 20.0391 0 15.3418 0C10.6543 0 7.44141 3.20312 7.44141 8.68164L7.44141 18.0469L7.58789 21.9922C7.60742 22.4316 7.94922 22.7832 8.38867 22.7832C8.82812 22.7832 9.16992 22.4316 9.18945 21.9922L9.33594 18.0469L9.33594 8.53516C9.33594 4.27734 11.748 1.89453 15.2441 1.89453C18.7402 1.89453 21.2793 4.27734 21.2793 8.53516L21.2793 11.7871C21.2793 12.373 21.6992 12.7539 22.2266 12.7539Z", fillAlpha = 0.85f)
        }
        return _sFArrowUturnDown!!
    }

private var _sFArrowUturnDown: ImageVector? = null
