package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowUpRightVideoFill (monochrome)
 * Viewport: 32.3242 x 20.4199
 */
public val SfSymbols.Monochrome.SFArrowUpRightVideoFill: ImageVector
    get() {
        if (_sFArrowUpRightVideoFill != null) {
            return _sFArrowUpRightVideoFill!!
        }
        _sFArrowUpRightVideoFill = sfIcon(
            name = "Monochrome.SFArrowUpRightVideoFill",
            viewportWidth = 32.3242f,
            viewportHeight = 20.4199f
        ) {
            addSfPath("M23.3008 4.00391L23.3008 16.3965C23.3008 18.9551 21.8066 20.4102 19.2383 20.4102L5.57617 20.4102C3.00781 20.4102 1.51367 18.9551 1.51367 16.3965L1.51367 4.00391C1.51367 1.45508 3.0957 0 5.57617 0L19.3359 0C21.9043 0 23.3008 1.45508 23.3008 4.00391ZM32.3242 3.66211L32.3242 16.7383C32.3242 17.7441 31.7188 18.4082 30.8008 18.4082C30.332 18.4082 29.8633 18.1738 29.4238 17.8027L24.8535 13.916L24.8535 6.47461L29.4238 2.60742C29.8633 2.23633 30.332 2.00195 30.8008 2.00195C31.7188 2.00195 32.3242 2.66602 32.3242 3.66211ZM9.375 5.10742C8.92578 5.10742 8.57422 5.47852 8.57422 5.9375C8.57422 6.38672 8.93555 6.69922 9.375 6.69922L11.5625 6.69922L15.2257 6.48773L13.0566 8.50586L7.48047 14.0918C7.28516 14.2871 7.20703 14.4727 7.19727 14.7168C7.20703 15.166 7.56836 15.498 8.03711 15.4883C8.30078 15.498 8.4668 15.4102 8.63281 15.2344L14.2188 9.64844L16.2347 7.49808L16.0156 11.1621L16.0156 13.3008C16.0156 13.7305 16.3379 14.0918 16.7773 14.0918C17.2461 14.0918 17.6074 13.75 17.6074 13.3008L17.6074 5.98633C17.6074 5.41992 17.3145 5.10742 16.7285 5.10742Z", fillAlpha = 0.85f)
        }
        return _sFArrowUpRightVideoFill!!
    }

private var _sFArrowUpRightVideoFill: ImageVector? = null
