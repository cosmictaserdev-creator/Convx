package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPhoneCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFPhoneCircleFill: ImageVector
    get() {
        if (_sFPhoneCircleFill != null) {
            return _sFPhoneCircleFill!!
        }
        _sFPhoneCircleFill = sfIcon(
            name = "Monochrome.SFPhoneCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7344C25.4395 19.7461 19.7266 25.459 12.7148 25.459C5.71289 25.459 0 19.7461 0 12.7344C0 5.73242 5.71289 0.0195312 12.7148 0.0195312C19.7266 0.0195312 25.4395 5.73242 25.4395 12.7344ZM6.94336 6.81641C6.34766 7.38281 6.02539 8.18359 6.02539 9.08203C6.02539 11.1328 7.59766 13.6035 9.70703 15.7129C11.8164 17.8223 14.3457 19.4336 16.377 19.4336C17.334 19.4336 18.1543 19.0527 18.7012 18.4473C19.2383 17.8516 19.4043 17.4219 19.4043 17.0215C19.4043 16.7285 19.2285 16.4551 18.7402 16.1133L16.9922 14.8633C16.5527 14.541 16.3672 14.4922 16.1035 14.4922C15.8887 14.4922 15.6934 14.5312 15.3223 14.7461L14.1699 15.3711C14.043 15.4297 13.9746 15.4688 13.877 15.4688C13.7402 15.4688 13.6523 15.4199 13.5254 15.3711C12.9688 15.1074 12.207 14.5117 11.5137 13.8379C10.8496 13.1641 10.3125 12.4609 10.0391 11.9238C10.0098 11.8359 9.96094 11.7285 9.96094 11.6016C9.96094 11.5234 10.0098 11.4355 10.0684 11.3379L10.7324 10.1855C10.9277 9.87305 10.9766 9.70703 10.9766 9.45312C10.9766 9.16992 10.8887 8.87695 10.6055 8.49609L9.4043 6.81641C9.0625 6.32812 8.80859 6.04492 8.41797 6.04492C7.93945 6.04492 7.36328 6.41602 6.94336 6.81641Z", fillAlpha = 0.85f)
        }
        return _sFPhoneCircleFill!!
    }

private var _sFPhoneCircleFill: ImageVector? = null
