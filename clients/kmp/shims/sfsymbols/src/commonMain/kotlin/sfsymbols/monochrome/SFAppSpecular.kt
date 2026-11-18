package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFAppSpecular (monochrome)
 * Viewport: 23.5699 x 23.1569
 */
public val SfSymbols.Monochrome.SFAppSpecular: ImageVector
    get() {
        if (_sFAppSpecular != null) {
            return _sFAppSpecular!!
        }
        _sFAppSpecular = sfIcon(
            name = "Monochrome.SFAppSpecular",
            viewportWidth = 23.5699f,
            viewportHeight = 23.1569f
        ) {
            addSfPath("M0.0124655 6.38802L0.617934 17.6771C0.637466 18.0091 0.891372 18.2826 1.2234 18.2826C1.5652 18.2826 1.81911 18.0091 1.82887 17.6771L2.13161 6.37825C2.19997 3.57552 3.57692 2.19857 6.38942 2.13021L17.6785 1.82747C18.0105 1.81771 18.2839 1.55404 18.2839 1.222C18.2839 0.889973 18.0105 0.636067 17.6785 0.616536L6.38942 0.0110672C2.88356-0.17448-0.221909 1.97396 0.0124655 6.38802ZM23.1863 16.7689L22.5808 5.47982C22.5613 5.14779 22.3074 4.87435 21.9754 4.87435C21.6433 4.87435 21.3797 5.14779 21.3699 5.47982L21.0769 16.7786C20.9988 19.5814 19.6218 20.9583 16.8191 21.0267L5.52028 21.3294C5.18825 21.3392 4.91481 21.6029 4.91481 21.9349C4.91481 22.2669 5.18825 22.5208 5.52028 22.5404L16.8093 23.1458C20.3152 23.3314 23.4207 21.1829 23.1863 16.7689Z", fillAlpha = 0.85f)
        }
        return _sFAppSpecular!!
    }

private var _sFAppSpecular: ImageVector? = null
