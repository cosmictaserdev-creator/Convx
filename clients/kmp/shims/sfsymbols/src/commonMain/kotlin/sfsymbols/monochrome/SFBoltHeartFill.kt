package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBoltHeartFill (monochrome)
 * Viewport: 25.0879 x 23.4668
 */
public val SfSymbols.Monochrome.SFBoltHeartFill: ImageVector
    get() {
        if (_sFBoltHeartFill != null) {
            return _sFBoltHeartFill!!
        }
        _sFBoltHeartFill = sfIcon(
            name = "Monochrome.SFBoltHeartFill",
            viewportWidth = 25.0879f,
            viewportHeight = 23.4668f
        ) {
            addSfPath("M24.7266 8.1543C24.7266 13.457 20.1758 18.6523 13.1934 23.1543C12.9492 23.3105 12.6074 23.4668 12.3633 23.4668C12.1289 23.4668 11.7871 23.3105 11.543 23.1543C4.55078 18.6523 0 13.457 0 8.1543C0 3.79883 2.99805 0.693359 6.91406 0.693359C9.31641 0.693359 11.2695 2.05078 12.3633 4.11133C13.4668 2.04102 15.4199 0.693359 17.8125 0.693359C21.7285 0.693359 24.7266 3.79883 24.7266 8.1543ZM13.7402 6.44531L8.49609 13.0273C8.39844 13.1445 8.34961 13.2715 8.34961 13.3789C8.34961 13.5938 8.51562 13.75 8.74023 13.75L11.9922 13.75L10.2441 18.4277C10.0293 18.9844 10.6348 19.2969 10.9961 18.8379L16.2402 12.2559C16.3379 12.1387 16.3867 12.0117 16.3867 11.9043C16.3867 11.6895 16.2207 11.5332 15.9961 11.5332L12.7441 11.5332L14.4922 6.85547C14.707 6.29883 14.1016 5.98633 13.7402 6.44531Z", fillAlpha = 0.85f)
        }
        return _sFBoltHeartFill!!
    }

private var _sFBoltHeartFill: ImageVector? = null
