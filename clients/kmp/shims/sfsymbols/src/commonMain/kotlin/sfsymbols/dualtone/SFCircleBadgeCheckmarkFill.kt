package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCircleBadgeCheckmarkFill (dualtone)
 * Viewport: 37.666 x 28.8574
 */
public val SfSymbols.Dualtone.SFCircleBadgeCheckmarkFill: ImageVector
    get() {
        if (_sFCircleBadgeCheckmarkFill != null) {
            return _sFCircleBadgeCheckmarkFill!!
        }
        _sFCircleBadgeCheckmarkFill = sfIcon(
            name = "Dualtone.SFCircleBadgeCheckmarkFill",
            viewportWidth = 37.666f,
            viewportHeight = 28.8574f
        ) {
            addSfPath("M31.3183 13.2677C30.8254 13.1668 30.3157 13.1152 29.7949 13.1152C25.4785 13.1152 21.9141 16.6699 21.9141 20.9863C21.9141 22.9101 22.617 24.6794 23.7808 26.0487C22.2138 26.7518 20.4771 27.1387 18.6523 27.1387C11.6504 27.1387 5.9375 21.4258 5.9375 14.4238C5.9375 7.41211 11.6504 1.69922 18.6523 1.69922C25.2738 1.69922 30.737 6.79392 31.3183 13.2677Z", fillAlpha = 0.425f)
            addSfPath("M36.1035 20.9863C36.1035 24.4434 33.2227 27.3047 29.7949 27.3047C26.3281 27.3047 23.4766 24.4629 23.4766 20.9863C23.4766 17.5293 26.3281 14.668 29.7949 14.668C33.252 14.668 36.1035 17.5195 36.1035 20.9863ZM32.0605 18.2324L28.9941 22.4805L27.4219 20.791C27.2949 20.6543 27.1191 20.5371 26.8652 20.5371C26.4648 20.5371 26.1426 20.8496 26.1426 21.2598C26.1426 21.4258 26.2012 21.6309 26.3379 21.7871L28.4375 24.0918C28.5938 24.2578 28.8281 24.3457 29.0234 24.3457C29.2773 24.3457 29.502 24.2383 29.6289 24.0625L33.2324 19.0625C33.3398 18.916 33.3984 18.7598 33.3984 18.623C33.3984 18.2227 33.0566 17.9102 32.6758 17.9102C32.4121 17.9102 32.1973 18.0371 32.0605 18.2324Z", fillAlpha = 0.85f)
        }
        return _sFCircleBadgeCheckmarkFill!!
    }

private var _sFCircleBadgeCheckmarkFill: ImageVector? = null
