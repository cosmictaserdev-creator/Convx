package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLeafFill (monochrome)
 * Viewport: 27.1973 x 23.6426
 */
public val SfSymbols.Monochrome.SFLeafFill: ImageVector
    get() {
        if (_sFLeafFill != null) {
            return _sFLeafFill!!
        }
        _sFLeafFill = sfIcon(
            name = "Monochrome.SFLeafFill",
            viewportWidth = 27.1973f,
            viewportHeight = 23.6426f
        ) {
            addSfPath("M0.351562 1.35742C0.107422 2.67578 0 4.33594 0 5.41992C0 14.7852 5.52734 20.9863 13.9355 20.9863C19.0332 20.9863 21.7871 18.0664 22.4512 16.6406L21.0742 16.709C22.6465 18.4082 23.3203 20.1172 24.1797 22.8418C24.3652 23.4277 24.7754 23.6426 25.2051 23.6426C26.1133 23.6426 26.8359 22.8516 26.8359 21.7578C26.8359 19.9902 24.4238 16.9824 23.1348 15.7715C17.7246 10.7227 9.58008 13.6816 7.23633 8.20312C7.05078 7.77344 7.5 7.42188 7.90039 7.83203C12.1875 12.1191 17.666 8.49609 23.1445 13.5254C23.5254 13.8672 23.9941 13.6914 24.0625 13.2617C24.1309 12.8809 24.1602 12.3047 24.1602 11.7188C24.1602 5.6543 19.9512 2.76367 13.9746 2.76367C11.9922 2.76367 9.74609 3.19336 7.91992 3.19336C5.75195 3.19336 3.42773 2.95898 1.69922 1.00586C1.23047 0.507812 0.517578 0.585938 0.351562 1.35742Z", fillAlpha = 0.85f)
        }
        return _sFLeafFill!!
    }

private var _sFLeafFill: ImageVector? = null
