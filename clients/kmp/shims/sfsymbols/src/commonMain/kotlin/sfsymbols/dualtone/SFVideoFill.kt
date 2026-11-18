package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFVideoFill (dualtone)
 * Viewport: 32.3242 x 20.4199
 */
public val SfSymbols.Dualtone.SFVideoFill: ImageVector
    get() {
        if (_sFVideoFill != null) {
            return _sFVideoFill!!
        }
        _sFVideoFill = sfIcon(
            name = "Dualtone.SFVideoFill",
            viewportWidth = 32.3242f,
            viewportHeight = 20.4199f
        ) {
            addSfPath("M5.57617 20.4102L19.2383 20.4102C21.8066 20.4102 23.3008 18.9551 23.3008 16.3965L23.3008 4.00391C23.3008 1.45508 21.9043 0 19.3359 0L5.57617 0C3.0957 0 1.51367 1.45508 1.51367 4.00391L1.51367 16.3965C1.51367 18.9551 3.00781 20.4102 5.57617 20.4102ZM24.8535 13.916L29.4238 17.8027C29.8633 18.1738 30.332 18.4082 30.8008 18.4082C31.7188 18.4082 32.3242 17.7441 32.3242 16.7383L32.3242 3.66211C32.3242 2.66602 31.7188 2.00195 30.8008 2.00195C30.332 2.00195 29.8633 2.23633 29.4238 2.60742L24.8535 6.47461Z", fillAlpha = 0.85f)
        }
        return _sFVideoFill!!
    }

private var _sFVideoFill: ImageVector? = null
