package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFWaterbottleFill (dualtone)
 * Viewport: 14.0234 x 29.5801
 */
public val SfSymbols.Dualtone.SFWaterbottleFill: ImageVector
    get() {
        if (_sFWaterbottleFill != null) {
            return _sFWaterbottleFill!!
        }
        _sFWaterbottleFill = sfIcon(
            name = "Dualtone.SFWaterbottleFill",
            viewportWidth = 14.0234f,
            viewportHeight = 29.5801f
        ) {
            addSfPath("M3.39844 3.18359L10.2734 3.18359C10.8887 3.18359 11.3379 2.75391 11.3379 2.11914L11.3379 1.06445C11.3379 0.439453 10.8887 0 10.2734 0L3.39844 0C2.76367 0 2.33398 0.439453 2.33398 1.06445L2.33398 2.11914C2.33398 2.75391 2.76367 3.18359 3.39844 3.18359ZM0.00976562 9.30664L13.6523 9.30664C13.6133 7.82227 13.3887 6.78711 12.5586 5.67383L12.207 5.19531C11.8848 4.74609 11.3672 4.45312 10.8105 4.45312L2.85156 4.45312C2.29492 4.45312 1.77734 4.74609 1.44531 5.19531L1.10352 5.67383C0.273438 6.78711 0.0488281 7.82227 0.00976562 9.30664ZM0 10.8203L0 23.6426L13.6621 23.6426L13.6621 10.8203ZM6.82617 21.5234C5.00977 21.5234 3.81836 20.3613 3.81836 18.6133C3.81836 17.7148 4.22852 16.9922 4.43359 16.5234C4.91211 15.498 5.69336 14.1699 6.36719 13.1543C6.49414 12.9492 6.66992 12.8418 6.82617 12.8418C7.00195 12.8418 7.1582 12.9492 7.28516 13.1543C7.95898 14.1699 8.75977 15.498 9.21875 16.5234C9.44336 16.9922 9.83398 17.7148 9.83398 18.6133C9.83398 20.3613 8.64258 21.5234 6.82617 21.5234ZM0 25.1562L0 25.4785C0 28.1445 1.31836 29.5605 3.75977 29.5605L9.89258 29.5605C12.3438 29.5605 13.6621 28.1445 13.6621 25.4785L13.6621 25.1562Z", fillAlpha = 0.85f)
        }
        return _sFWaterbottleFill!!
    }

private var _sFWaterbottleFill: ImageVector? = null
