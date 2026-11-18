package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBoltSlashCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFBoltSlashCircleFill: ImageVector
    get() {
        if (_sFBoltSlashCircleFill != null) {
            return _sFBoltSlashCircleFill!!
        }
        _sFBoltSlashCircleFill = sfIcon(
            name = "Monochrome.SFBoltSlashCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7344C25.4395 19.7461 19.7266 25.459 12.7148 25.459C5.71289 25.459 0 19.7461 0 12.7344C0 5.73242 5.71289 0.0195312 12.7148 0.0195312C19.7266 0.0195312 25.4395 5.73242 25.4395 12.7344ZM7.22656 13.1934C7.08984 13.3594 7.03125 13.5254 7.03125 13.6719C7.03125 13.9746 7.25586 14.1895 7.56836 14.1895L12.0215 14.1895L9.63867 20.6055C9.33594 21.377 10.166 21.7871 10.6641 21.1719L14.4446 16.427L8.9934 10.9758ZM6.2793 6.29883C6.07422 6.51367 6.08398 6.85547 6.2793 7.05078L18.4082 19.1699C18.6035 19.375 18.9551 19.375 19.1602 19.1699C19.3652 18.9453 19.3652 18.623 19.1602 18.418L7.04102 6.29883C6.83594 6.08398 6.50391 6.08398 6.2793 6.29883ZM13.1011 11.1426L16.191 14.235L17.8613 12.1387C17.9883 11.9727 18.0566 11.8066 18.0566 11.6602C18.0566 11.3574 17.8223 11.1426 17.5195 11.1426ZM14.4238 4.16016L10.7418 8.78141L13.0758 11.1173L15.4492 4.72656C15.752 3.95508 14.9219 3.54492 14.4238 4.16016Z", fillAlpha = 0.85f)
        }
        return _sFBoltSlashCircleFill!!
    }

private var _sFBoltSlashCircleFill: ImageVector? = null
