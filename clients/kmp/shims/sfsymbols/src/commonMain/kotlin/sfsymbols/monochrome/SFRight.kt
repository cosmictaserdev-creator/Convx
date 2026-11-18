package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRight (monochrome)
 * Viewport: 12.793 x 18.6426
 */
public val SfSymbols.Monochrome.SFRight: ImageVector
    get() {
        if (_sFRight != null) {
            return _sFRight!!
        }
        _sFRight = sfIcon(
            name = "Monochrome.SFRight",
            viewportWidth = 12.793f,
            viewportHeight = 18.6426f
        ) {
            addSfPath("M0.9375 18.6426C1.50391 18.6426 1.875 18.2715 1.875 17.6855L1.875 11.1523L6.10352 11.1523C6.24023 11.1523 6.41602 11.1426 6.54297 11.1328L10.4883 18.0762C10.7129 18.4766 10.9766 18.6426 11.377 18.6426C11.9043 18.6426 12.3242 18.2812 12.3242 17.7637C12.3242 17.5586 12.2461 17.373 12.1191 17.1484L8.4668 10.8301C10.8887 10.1562 12.3145 8.22266 12.3145 5.67383C12.3145 2.37305 10 0.283203 6.41602 0.283203L0.9375 0.283203C0.400391 0.283203 0 0.683594 0 1.25L0 17.6855C0 18.2715 0.371094 18.6426 0.9375 18.6426ZM1.875 9.45312L1.875 1.99219L6.23047 1.99219C8.86719 1.99219 10.3906 3.34961 10.3906 5.72266C10.3906 8.0957 8.91602 9.45312 6.28906 9.45312Z", fillAlpha = 0.85f)
        }
        return _sFRight!!
    }

private var _sFRight: ImageVector? = null
