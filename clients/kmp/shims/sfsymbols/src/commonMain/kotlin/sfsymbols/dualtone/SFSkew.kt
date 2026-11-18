package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSkew (dualtone)
 * Viewport: 29.8438 x 27.3926
 */
public val SfSymbols.Dualtone.SFSkew: ImageVector
    get() {
        if (_sFSkew != null) {
            return _sFSkew!!
        }
        _sFSkew = sfIcon(
            name = "Dualtone.SFSkew",
            viewportWidth = 29.8438f,
            viewportHeight = 27.3926f
        ) {
            addSfPath("M0 18.6035C0 20.0879 1.21094 21.2988 2.68555 21.2988C3.75977 21.2988 4.6875 20.6543 5.10742 19.7461L24.209 25.3125C24.4629 26.5039 25.5371 27.3926 26.8066 27.3926C28.2812 27.3926 29.4824 26.1816 29.4824 24.707C29.4824 23.5547 28.75 22.5684 27.7344 22.207L27.2559 9.54102C28.3594 9.22852 29.1504 8.21289 29.1504 7.00195C29.1504 5.52734 27.9492 4.31641 26.4746 4.31641C25.498 4.31641 24.6484 4.84375 24.1699 5.625L13.4668 2.69531C13.4375 1.24023 12.2363 0.0585938 10.791 0.0585938C9.31641 0.0585938 8.10547 1.26953 8.10547 2.74414C8.10547 3.55469 8.4668 4.27734 9.02344 4.77539L3.19336 16.0059C3.01758 15.9473 2.86133 15.918 2.68555 15.918C1.21094 15.918 0 17.1289 0 18.6035ZM4.59961 16.7383L10.4395 5.37109C11.4746 5.56641 12.5293 5.06836 12.998 4.16992L23.8574 7.13867C23.8477 8.26172 24.6387 9.28711 25.7324 9.58984L26.1328 22.1094C25.2734 22.3242 24.5703 22.9688 24.2969 23.7988L5.30273 18.2324C5.23438 17.6465 5.0293 17.168 4.59961 16.7383Z", fillAlpha = 0.85f)
        }
        return _sFSkew!!
    }

private var _sFSkew: ImageVector? = null
