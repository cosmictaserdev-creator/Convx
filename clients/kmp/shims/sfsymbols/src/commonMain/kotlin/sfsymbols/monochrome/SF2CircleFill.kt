package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SF2CircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SF2CircleFill: ImageVector
    get() {
        if (_sF2CircleFill != null) {
            return _sF2CircleFill!!
        }
        _sF2CircleFill = sfIcon(
            name = "Monochrome.SF2CircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM8.99414 9.20898C8.93555 9.38477 8.92578 9.53125 8.92578 9.69727C8.92578 10.127 9.21875 10.4199 9.66797 10.4199C10.0781 10.4199 10.293 10.1758 10.459 9.83398C10.7617 8.93555 11.5332 8.07617 12.8516 8.07617C14.1602 8.07617 15.0195 8.83789 15.0195 9.9707C15.0195 10.9082 14.3066 11.709 13.5059 12.5684L9.29688 17.1191C9.10156 17.334 8.99414 17.5586 8.99414 17.793C8.99414 18.252 9.29688 18.5449 9.80469 18.5449L16.4258 18.5449C16.8652 18.5449 17.1777 18.252 17.1777 17.8027C17.1777 17.3438 16.8652 17.0605 16.4258 17.0605L11.4746 17.0605L11.4746 16.9824L14.6973 13.4863C15.8887 12.2168 16.6211 11.2109 16.6211 9.85352C16.6211 7.91992 15.0977 6.64062 12.7832 6.64062C10.9668 6.64062 9.36523 7.74414 8.99414 9.20898Z", fillAlpha = 0.85f)
        }
        return _sF2CircleFill!!
    }

private var _sF2CircleFill: ImageVector? = null
