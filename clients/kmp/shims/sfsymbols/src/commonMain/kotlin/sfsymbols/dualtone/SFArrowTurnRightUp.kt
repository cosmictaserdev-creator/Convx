package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowTurnRightUp (dualtone)
 * Viewport: 19.707 x 23.6816
 */
public val SfSymbols.Dualtone.SFArrowTurnRightUp: ImageVector
    get() {
        if (_sFArrowTurnRightUp != null) {
            return _sFArrowTurnRightUp!!
        }
        _sFArrowTurnRightUp = sfIcon(
            name = "Dualtone.SFArrowTurnRightUp",
            viewportWidth = 19.707f,
            viewportHeight = 23.6816f
        ) {
            addSfPath("M9.90234 1.97266L13.3008 5.68359L17.7539 10.1367C17.9199 10.3027 18.1738 10.3906 18.4277 10.3906C18.9551 10.3906 19.3457 9.99023 19.3457 9.45312C19.3457 9.19922 19.2578 8.98438 19.0527 8.76953L10.6055 0.322266C10.4004 0.107422 10.1562 0 9.89258 0C9.62891 0 9.38477 0.107422 9.18945 0.322266L0.732422 8.76953C0.537109 8.98438 0.439453 9.19922 0.439453 9.45312C0.439453 9.99023 0.830078 10.3906 1.35742 10.3906C1.61133 10.3906 1.86523 10.3027 2.03125 10.1367L6.48438 5.68359ZM2.5 23.6816C8.22266 23.6816 10.8398 21.3281 10.8398 15.6055L10.8398 6.23047L10.752 1.97266C10.7422 1.50391 10.3613 1.12305 9.90234 1.12305C9.43359 1.12305 9.05273 1.50391 9.04297 1.97266L8.94531 6.23047L8.95508 15.5176C8.96484 20.127 6.98242 21.8164 2.40234 21.8164C1.78711 21.8066 1.28906 21.7871 0.947266 21.7773C0.400391 21.7773 0 22.1387 0 22.6953C0 23.2422 0.380859 23.5059 0.810547 23.6035C1.15234 23.6719 1.77734 23.6816 2.5 23.6816Z", fillAlpha = 0.85f)
        }
        return _sFArrowTurnRightUp!!
    }

private var _sFArrowTurnRightUp: ImageVector? = null
