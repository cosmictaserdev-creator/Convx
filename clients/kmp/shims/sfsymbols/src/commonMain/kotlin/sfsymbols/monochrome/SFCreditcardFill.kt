package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCreditcardFill (monochrome)
 * Viewport: 29.7656 x 21.2598
 */
public val SfSymbols.Monochrome.SFCreditcardFill: ImageVector
    get() {
        if (_sFCreditcardFill != null) {
            return _sFCreditcardFill!!
        }
        _sFCreditcardFill = sfIcon(
            name = "Monochrome.SFCreditcardFill",
            viewportWidth = 29.7656f,
            viewportHeight = 21.2598f
        ) {
            addSfPath("M5.07812 17.4219C4.33594 17.4219 3.83789 16.9238 3.83789 16.2109L3.83789 13.877C3.83789 13.1641 4.33594 12.666 5.07812 12.666L8.16406 12.666C8.89648 12.666 9.4043 13.1641 9.4043 13.877L9.4043 16.2109C9.4043 16.9238 8.89648 17.4219 8.16406 17.4219ZM0 7.74414L0 4.91211L29.4043 4.91211L29.4043 7.74414ZM3.79883 21.2598L25.5957 21.2598C28.125 21.2598 29.4043 19.9805 29.4043 17.4902L29.4043 3.79883C29.4043 1.30859 28.125 0.0292969 25.5957 0.0292969L3.79883 0.0292969C1.2793 0.0292969 0 1.29883 0 3.79883L0 17.4902C0 19.9902 1.2793 21.2598 3.79883 21.2598Z", fillAlpha = 0.85f)
        }
        return _sFCreditcardFill!!
    }

private var _sFCreditcardFill: ImageVector? = null
