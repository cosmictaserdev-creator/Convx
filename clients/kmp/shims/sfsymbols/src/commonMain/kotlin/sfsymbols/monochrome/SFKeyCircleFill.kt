package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFKeyCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFKeyCircleFill: ImageVector
    get() {
        if (_sFKeyCircleFill != null) {
            return _sFKeyCircleFill!!
        }
        _sFKeyCircleFill = sfIcon(
            name = "Monochrome.SFKeyCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7344C25.4395 19.7461 19.7266 25.459 12.7148 25.459C5.71289 25.459 0 19.7461 0 12.7344C0 5.73242 5.71289 0.0195312 12.7148 0.0195312C19.7266 0.0195312 25.4395 5.73242 25.4395 12.7344ZM8.53516 9.0918C8.53516 10.8594 9.58008 12.4023 11.2402 13.0273L11.2402 19.5703C11.2402 19.707 11.2793 19.834 11.3965 19.9707L12.5 21.0547C12.6074 21.1719 12.8125 21.2012 12.9395 21.0547L15.0391 18.9746C15.1562 18.8574 15.1465 18.6719 15.0391 18.5547L13.7109 17.2461L15.5176 15.4492C15.6348 15.3418 15.6348 15.1465 15.5078 15.0293L13.7109 13.1934C15.7617 12.4023 16.9043 10.9082 16.9043 9.0918C16.9043 6.78711 15.0488 4.91211 12.7148 4.91211C10.3906 4.91211 8.53516 6.77734 8.53516 9.0918ZM13.9062 7.76367C13.9062 8.41797 13.3789 8.95508 12.7148 8.95508C12.0508 8.95508 11.5332 8.41797 11.5332 7.76367C11.5332 7.09961 12.0508 6.58203 12.7148 6.58203C13.3789 6.58203 13.9062 7.09961 13.9062 7.76367Z", fillAlpha = 0.85f)
        }
        return _sFKeyCircleFill!!
    }

private var _sFKeyCircleFill: ImageVector? = null
