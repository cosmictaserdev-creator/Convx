package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowTurnDownRight (dualtone)
 * Viewport: 23.9648 x 20.6934
 */
public val SfSymbols.Dualtone.SFArrowTurnDownRight: ImageVector
    get() {
        if (_sFArrowTurnDownRight != null) {
            return _sFArrowTurnDownRight!!
        }
        _sFArrowTurnDownRight = sfIcon(
            name = "Dualtone.SFArrowTurnDownRight",
            viewportWidth = 23.9648f,
            viewportHeight = 20.6934f
        ) {
            addSfPath("M21.6309 10.0293L16.8359 14.4629L13.457 17.8906C13.2812 18.0664 13.1934 18.3105 13.1934 18.5645C13.1934 19.1016 13.6035 19.4922 14.1309 19.4922C14.3848 19.4922 14.6191 19.3945 14.8828 19.1406L23.2617 10.752C23.4863 10.5371 23.6035 10.293 23.6035 10.0293C23.6035 9.76562 23.4863 9.51172 23.2617 9.29688L14.8242 0.859375C14.6094 0.654297 14.3848 0.556641 14.1309 0.556641C13.6035 0.556641 13.1934 0.957031 13.1934 1.48438C13.1934 1.73828 13.291 1.99219 13.457 2.1582L16.8359 5.58594ZM21.6211 9.23828L17.793 9.08203L8.34961 9.07227C3.68164 9.07227 1.92383 7.14844 1.92383 2.42188C1.93359 1.79688 1.95312 1.29883 1.96289 0.947266C1.96289 0.390625 1.5625 0 1.00586 0C0.439453 0 0.185547 0.371094 0.0878906 0.810547C0.0195312 1.15234 0 1.78711 0 2.51953C0 8.45703 2.45117 10.9766 8.26172 10.9766L17.793 10.9766L21.6113 10.8301C22.0508 10.8105 22.4121 10.4688 22.4121 10.0293C22.4121 9.59961 22.0605 9.25781 21.6211 9.23828Z", fillAlpha = 0.85f)
        }
        return _sFArrowTurnDownRight!!
    }

private var _sFArrowTurnDownRight: ImageVector? = null
