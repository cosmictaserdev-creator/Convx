package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSwitch2 (monochrome)
 * Viewport: 24.0625 x 23.623
 */
public val SfSymbols.Monochrome.SFSwitch2: ImageVector
    get() {
        if (_sFSwitch2 != null) {
            return _sFSwitch2!!
        }
        _sFSwitch2 = sfIcon(
            name = "Monochrome.SFSwitch2",
            viewportWidth = 24.0625f,
            viewportHeight = 23.623f
        ) {
            addSfPath("M5.25391 23.5547L18.4473 23.5547C21.3867 23.5547 23.7012 21.5039 23.7012 18.5059C23.7012 15.5176 21.3867 13.4668 18.4473 13.4668L5.25391 13.4668C2.31445 13.4668 0 15.5176 0 18.5059C0 21.5039 2.31445 23.5547 5.25391 23.5547ZM14.7266 21.8848C12.8223 21.8848 11.2598 20.5176 11.2598 18.4961C11.2598 16.4941 12.8223 15.127 14.7266 15.127L18.6328 15.127C20.5469 15.127 22.0996 16.4941 22.0996 18.5059C22.0996 20.5176 20.5469 21.8848 18.6328 21.8848Z", fillAlpha = 0.85f)
            addSfPath("M5.80078 11.2793L17.9004 11.2793C21.0938 11.2793 23.7012 8.99414 23.7012 5.64453C23.7012 2.29492 21.0938 0 17.9004 0L5.80078 0C2.60742 0 0 2.29492 0 5.64453C0 8.99414 2.60742 11.2793 5.80078 11.2793ZM5.80078 9.64844C3.54492 9.64844 1.68945 8.02734 1.68945 5.64453C1.68945 3.26172 3.54492 1.64062 5.80078 1.64062L17.9004 1.64062C20.1562 1.64062 22.0117 3.27148 22.0117 5.64453C22.0117 8.01758 20.1562 9.64844 17.9004 9.64844Z", fillAlpha = 0.85f)
            addSfPath("M5.80078 8.80859L9.56055 8.80859C11.3477 8.80859 12.8125 7.51953 12.8125 5.64453C12.8125 3.75977 11.3477 2.4707 9.56055 2.4707L5.80078 2.4707C4.01367 2.4707 2.55859 3.75977 2.55859 5.63477C2.55859 7.51953 4.01367 8.80859 5.80078 8.80859Z", fillAlpha = 0.85f)
        }
        return _sFSwitch2!!
    }

private var _sFSwitch2: ImageVector? = null
