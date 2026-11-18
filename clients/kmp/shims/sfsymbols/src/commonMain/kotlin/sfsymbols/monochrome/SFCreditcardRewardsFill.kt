package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCreditcardRewardsFill (monochrome)
 * Viewport: 29.7656 x 21.2598
 */
public val SfSymbols.Monochrome.SFCreditcardRewardsFill: ImageVector
    get() {
        if (_sFCreditcardRewardsFill != null) {
            return _sFCreditcardRewardsFill!!
        }
        _sFCreditcardRewardsFill = sfIcon(
            name = "Monochrome.SFCreditcardRewardsFill",
            viewportWidth = 29.7656f,
            viewportHeight = 21.2598f
        ) {
            addSfPath("M0 7.74414L0 4.91211L29.4043 4.91211L29.4043 7.74414ZM5.41016 17.7051C5.09766 17.9395 4.75586 17.6953 4.87305 17.3242L5.58594 15.1953L3.75977 13.8965C3.4668 13.6719 3.56445 13.2422 3.96484 13.252L6.20117 13.2617L6.89453 11.123C7.00195 10.7715 7.43164 10.7715 7.53906 11.123L8.23242 13.2617L10.459 13.252C10.8594 13.2422 10.9473 13.6816 10.6641 13.8965L8.84766 15.1953L9.56055 17.3242C9.67773 17.6953 9.33594 17.9395 9.02344 17.7051L7.2168 16.3867ZM3.79883 21.2598L25.5957 21.2598C28.125 21.2598 29.4043 19.9805 29.4043 17.4902L29.4043 3.79883C29.4043 1.30859 28.125 0.0292969 25.5957 0.0292969L3.79883 0.0292969C1.2793 0.0292969 0 1.29883 0 3.79883L0 17.4902C0 19.9902 1.2793 21.2598 3.79883 21.2598Z", fillAlpha = 0.85f)
        }
        return _sFCreditcardRewardsFill!!
    }

private var _sFCreditcardRewardsFill: ImageVector? = null
