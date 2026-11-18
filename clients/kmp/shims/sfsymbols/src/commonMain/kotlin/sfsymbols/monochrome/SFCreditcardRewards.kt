package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCreditcardRewards (monochrome)
 * Viewport: 29.7656 x 21.2598
 */
public val SfSymbols.Monochrome.SFCreditcardRewards: ImageVector
    get() {
        if (_sFCreditcardRewards != null) {
            return _sFCreditcardRewards!!
        }
        _sFCreditcardRewards = sfIcon(
            name = "Monochrome.SFCreditcardRewards",
            viewportWidth = 29.7656f,
            viewportHeight = 21.2598f
        ) {
            addSfPath("M0.898438 7.62695L28.5352 7.62695L28.5352 4.79492L0.898438 4.79492ZM5.44922 17.334L7.2168 16.0352L8.98438 17.334C9.28711 17.5586 9.60938 17.3242 9.50195 16.9531L8.80859 14.873L10.5859 13.6035C10.8594 13.4082 10.7715 12.9785 10.3809 12.9883L8.20312 12.998L7.5293 10.9082C7.42188 10.5664 7.01172 10.5664 6.9043 10.9082L6.23047 12.998L4.04297 12.9883C3.65234 12.9785 3.55469 13.3887 3.83789 13.6133L5.625 14.873L4.93164 16.9531C4.81445 17.3242 5.14648 17.5586 5.44922 17.334ZM3.79883 21.2598L25.5957 21.2598C28.125 21.2598 29.4043 19.9805 29.4043 17.4902L29.4043 3.79883C29.4043 1.30859 28.125 0.0292969 25.5957 0.0292969L3.79883 0.0292969C1.2793 0.0292969 0 1.29883 0 3.79883L0 17.4902C0 19.9902 1.2793 21.2598 3.79883 21.2598ZM3.83789 19.5215C2.4707 19.5215 1.72852 18.7988 1.72852 17.4023L1.72852 3.88672C1.72852 2.48047 2.4707 1.75781 3.83789 1.75781L25.5664 1.75781C26.9043 1.75781 27.666 2.48047 27.666 3.88672L27.666 17.4023C27.666 18.7988 26.9043 19.5215 25.5664 19.5215Z", fillAlpha = 0.85f)
        }
        return _sFCreditcardRewards!!
    }

private var _sFCreditcardRewards: ImageVector? = null
