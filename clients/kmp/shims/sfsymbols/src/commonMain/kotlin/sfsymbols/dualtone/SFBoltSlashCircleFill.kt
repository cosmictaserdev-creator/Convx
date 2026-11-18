package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBoltSlashCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFBoltSlashCircleFill: ImageVector
    get() {
        if (_sFBoltSlashCircleFill != null) {
            return _sFBoltSlashCircleFill!!
        }
        _sFBoltSlashCircleFill = sfIcon(
            name = "Dualtone.SFBoltSlashCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.459C19.7266 25.459 25.4395 19.7461 25.4395 12.7344C25.4395 5.73242 19.7266 0.0195312 12.7148 0.0195312C5.71289 0.0195312 0 5.73242 0 12.7344C0 19.7461 5.71289 25.459 12.7148 25.459Z", fillAlpha = 0.2125f)
            addSfPath("M14.4446 16.427L10.6641 21.1719C10.166 21.7871 9.33594 21.377 9.63867 20.6055L12.0215 14.1895L7.56836 14.1895C7.25586 14.1895 7.03125 13.9746 7.03125 13.6719C7.03125 13.5254 7.08984 13.3594 7.22656 13.1934L8.9934 10.9758ZM7.04102 6.29883L19.1602 18.418C19.3652 18.623 19.3652 18.9453 19.1602 19.1699C18.9551 19.375 18.6035 19.375 18.4082 19.1699L6.2793 7.05078C6.08398 6.85547 6.07422 6.51367 6.2793 6.29883C6.50391 6.08398 6.83594 6.08398 7.04102 6.29883ZM18.0566 11.6602C18.0566 11.8066 17.9883 11.9727 17.8613 12.1387L16.191 14.235L13.1011 11.1426L17.5195 11.1426C17.8223 11.1426 18.0566 11.3574 18.0566 11.6602ZM15.4492 4.72656L13.0758 11.1173L10.7418 8.78141L14.4238 4.16016C14.9219 3.54492 15.752 3.95508 15.4492 4.72656Z", fillAlpha = 0.85f)
        }
        return _sFBoltSlashCircleFill!!
    }

private var _sFBoltSlashCircleFill: ImageVector? = null
