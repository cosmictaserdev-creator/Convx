package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCreditcard (dualtone)
 * Viewport: 29.7656 x 21.2598
 */
public val SfSymbols.Dualtone.SFCreditcard: ImageVector
    get() {
        if (_sFCreditcard != null) {
            return _sFCreditcard!!
        }
        _sFCreditcard = sfIcon(
            name = "Dualtone.SFCreditcard",
            viewportWidth = 29.7656f,
            viewportHeight = 21.2598f
        ) {
            addSfPath("M5.46875 17.0215L8.55469 17.0215C9.29688 17.0215 9.79492 16.5234 9.79492 15.8203L9.79492 13.4766C9.79492 12.7734 9.29688 12.2754 8.55469 12.2754L5.46875 12.2754C4.72656 12.2754 4.22852 12.7734 4.22852 13.4766L4.22852 15.8203C4.22852 16.5234 4.72656 17.0215 5.46875 17.0215ZM0.898438 7.62695L28.5352 7.62695L28.5352 4.79492L0.898438 4.79492ZM3.79883 21.2598L25.5957 21.2598C28.125 21.2598 29.4043 19.9805 29.4043 17.4902L29.4043 3.79883C29.4043 1.30859 28.125 0.0292969 25.5957 0.0292969L3.79883 0.0292969C1.2793 0.0292969 0 1.29883 0 3.79883L0 17.4902C0 19.9902 1.2793 21.2598 3.79883 21.2598ZM3.83789 19.5215C2.4707 19.5215 1.72852 18.7988 1.72852 17.4023L1.72852 3.88672C1.72852 2.48047 2.4707 1.75781 3.83789 1.75781L25.5664 1.75781C26.9043 1.75781 27.666 2.48047 27.666 3.88672L27.666 17.4023C27.666 18.7988 26.9043 19.5215 25.5664 19.5215Z", fillAlpha = 0.85f)
        }
        return _sFCreditcard!!
    }

private var _sFCreditcard: ImageVector? = null
