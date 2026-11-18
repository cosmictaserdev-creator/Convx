package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFHeart (dualtone)
 * Viewport: 25.0879 x 23.4668
 */
public val SfSymbols.Dualtone.SFHeart: ImageVector
    get() {
        if (_sFHeart != null) {
            return _sFHeart!!
        }
        _sFHeart = sfIcon(
            name = "Dualtone.SFHeart",
            viewportWidth = 25.0879f,
            viewportHeight = 23.4668f
        ) {
            addSfPath("M0 8.1543C0 13.457 4.55078 18.6523 11.543 23.1543C11.7871 23.3105 12.1289 23.4668 12.3633 23.4668C12.6074 23.4668 12.9492 23.3105 13.1934 23.1543C20.1758 18.6523 24.7266 13.457 24.7266 8.1543C24.7266 3.79883 21.7285 0.693359 17.7148 0.693359C15.4199 0.693359 13.4668 1.8457 12.3633 3.61328C11.2695 1.85547 9.31641 0.693359 7.01172 0.693359C2.99805 0.693359 0 3.79883 0 8.1543ZM1.72852 8.1543C1.72852 4.73633 3.96484 2.42188 6.99219 2.42188C9.3457 2.42188 10.7617 3.88672 11.6113 5.26367C11.9043 5.71289 12.0996 5.84961 12.3633 5.84961C12.6367 5.84961 12.8027 5.70312 13.125 5.26367C14.0137 3.90625 15.3906 2.42188 17.7344 2.42188C20.7617 2.42188 22.998 4.73633 22.998 8.1543C22.998 12.8711 18.0566 17.8906 12.5977 21.5234C12.4902 21.5918 12.4121 21.6504 12.3633 21.6504C12.3145 21.6504 12.2363 21.5918 12.1289 21.5234C6.66992 17.8906 1.72852 12.8711 1.72852 8.1543Z", fillAlpha = 0.85f)
        }
        return _sFHeart!!
    }

private var _sFHeart: ImageVector? = null
