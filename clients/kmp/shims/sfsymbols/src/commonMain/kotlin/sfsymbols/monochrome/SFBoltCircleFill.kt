package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBoltCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFBoltCircleFill: ImageVector
    get() {
        if (_sFBoltCircleFill != null) {
            return _sFBoltCircleFill!!
        }
        _sFBoltCircleFill = sfIcon(
            name = "Monochrome.SFBoltCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7344C25.4395 19.7461 19.7266 25.459 12.7148 25.459C5.71289 25.459 0 19.7461 0 12.7344C0 5.73242 5.71289 0.0195312 12.7148 0.0195312C19.7266 0.0195312 25.4395 5.73242 25.4395 12.7344ZM14.4238 4.16016L7.22656 13.1934C7.08984 13.3594 7.03125 13.5254 7.03125 13.6719C7.03125 13.9746 7.25586 14.1895 7.56836 14.1895L12.0215 14.1895L9.63867 20.6055C9.33594 21.377 10.166 21.7871 10.6641 21.1719L17.8613 12.1387C17.9883 11.9727 18.0566 11.8066 18.0566 11.6602C18.0566 11.3574 17.8223 11.1426 17.5195 11.1426L13.0664 11.1426L15.4492 4.72656C15.752 3.95508 14.9219 3.54492 14.4238 4.16016Z", fillAlpha = 0.85f)
        }
        return _sFBoltCircleFill!!
    }

private var _sFBoltCircleFill: ImageVector? = null
