package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowTurnLeftUp (monochrome)
 * Viewport: 19.707 x 23.6816
 */
public val SfSymbols.Monochrome.SFArrowTurnLeftUp: ImageVector
    get() {
        if (_sFArrowTurnLeftUp != null) {
            return _sFArrowTurnLeftUp!!
        }
        _sFArrowTurnLeftUp = sfIcon(
            name = "Monochrome.SFArrowTurnLeftUp",
            viewportWidth = 19.707f,
            viewportHeight = 23.6816f
        ) {
            addSfPath("M9.44336 1.97266L12.8613 5.68359L17.3145 10.1367C17.4805 10.3027 17.7344 10.3906 17.9883 10.3906C18.5156 10.3906 18.9062 9.99023 18.9062 9.45312C18.9062 9.19922 18.8184 8.98438 18.6133 8.76953L10.166 0.322266C9.96094 0.107422 9.7168 0 9.45312 0C9.18945 0 8.94531 0.107422 8.74023 0.322266L0.292969 8.76953C0.0878906 8.98438 0 9.19922 0 9.45312C0 9.99023 0.390625 10.3906 0.917969 10.3906C1.17188 10.3906 1.42578 10.3027 1.5918 10.1367L6.04492 5.68359ZM16.8457 23.6816C17.5684 23.6816 18.1934 23.6719 18.5352 23.6035C18.9648 23.5059 19.3457 23.2422 19.3457 22.6953C19.3457 22.1387 18.9453 21.7773 18.3984 21.7773C18.0566 21.7871 17.5586 21.8066 16.9434 21.8164C12.3633 21.8164 10.3809 20.127 10.3906 15.5176L10.4004 6.23047L10.3027 1.97266C10.293 1.50391 9.91211 1.12305 9.44336 1.12305C8.98438 1.12305 8.60352 1.50391 8.59375 1.97266L8.50586 6.23047L8.50586 15.6055C8.50586 21.3281 11.123 23.6816 16.8457 23.6816Z", fillAlpha = 0.85f)
        }
        return _sFArrowTurnLeftUp!!
    }

private var _sFArrowTurnLeftUp: ImageVector? = null
