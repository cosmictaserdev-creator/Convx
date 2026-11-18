package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFButtonVerticalLeftPress (monochrome)
 * Viewport: 30.3125 x 29.9121
 */
public val SfSymbols.Monochrome.SFButtonVerticalLeftPress: ImageVector
    get() {
        if (_sFButtonVerticalLeftPress != null) {
            return _sFButtonVerticalLeftPress!!
        }
        _sFButtonVerticalLeftPress = sfIcon(
            name = "Monochrome.SFButtonVerticalLeftPress",
            viewportWidth = 30.3125f,
            viewportHeight = 29.9121f
        ) {
            addSfPath("M28.0469 6.38672L28.0469 23.5254C28.0469 27.3926 25.4102 29.9121 21.3574 29.9121L18.0957 29.9121C14.5215 29.9121 12.207 27.2461 12.207 23.5254L12.207 19.7135L13.9355 18.2073L13.9355 23.5254C13.9355 26.1816 15.5859 28.1738 18.0957 28.1738C20.6152 28.1738 22.2559 26.1816 22.2559 23.5254L22.2559 6.38672C22.2559 3.7207 20.6152 1.72852 18.0957 1.72852C15.5859 1.72852 13.9355 3.7207 13.9355 6.38672L13.9355 11.6963L12.207 10.1901L12.207 6.38672C12.207 2.65625 14.5215 0 18.0957 2.82152e-08L21.3574 0C25.4102 0 28.0469 2.50977 28.0469 6.38672ZM23.9844 6.38672L23.9844 23.5254C23.9844 25.3751 23.4099 26.9642 22.3933 28.0937C24.7935 27.7161 26.3184 25.9982 26.3184 23.5254L26.3184 6.38672C26.3184 3.9122 24.7913 2.18607 22.3881 1.80842C23.4079 2.93734 23.9844 4.52881 23.9844 6.38672Z", fillAlpha = 0.85f)
            addSfPath("M2.48047 15.8594L11.0352 15.8594C11.5625 15.8594 11.9531 15.4688 11.9531 14.9414C11.9531 14.4336 11.5625 14.0332 11.0352 14.0332L2.48047 14.0332C1.95312 14.0332 1.5625 14.4336 1.5625 14.9414C1.5625 15.4688 1.95312 15.8594 2.48047 15.8594ZM9.14062 11.6895L9.14062 18.2227C9.14062 19.1309 10.0684 19.5117 10.752 18.916L14.4824 15.6738C14.9512 15.2637 14.9707 14.668 14.4824 14.2383L10.752 10.9961C10.0977 10.4102 9.14062 10.7422 9.14062 11.6895Z", fillAlpha = 0.85f)
        }
        return _sFButtonVerticalLeftPress!!
    }

private var _sFButtonVerticalLeftPress: ImageVector? = null
