package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFKeyCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFKeyCircleFill: ImageVector
    get() {
        if (_sFKeyCircleFill != null) {
            return _sFKeyCircleFill!!
        }
        _sFKeyCircleFill = sfIcon(
            name = "Dualtone.SFKeyCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.459C19.7266 25.459 25.4395 19.7461 25.4395 12.7344C25.4395 5.73242 19.7266 0.0195312 12.7148 0.0195312C5.71289 0.0195312 0 5.73242 0 12.7344C0 19.7461 5.71289 25.459 12.7148 25.459Z", fillAlpha = 0.2125f)
            addSfPath("M12.7148 4.91211C15.0488 4.91211 16.9043 6.78711 16.9043 9.0918C16.9043 10.9082 15.7617 12.4023 13.7109 13.1934L15.5078 15.0293C15.6348 15.1465 15.6348 15.3418 15.5176 15.4492L13.7109 17.2461L15.0391 18.5547C15.1465 18.6719 15.1562 18.8574 15.0391 18.9746L12.9395 21.0547C12.8125 21.2012 12.6074 21.1719 12.5 21.0547L11.3965 19.9707C11.2793 19.834 11.2402 19.707 11.2402 19.5703L11.2402 13.0273C9.58008 12.4023 8.53516 10.8594 8.53516 9.0918C8.53516 6.77734 10.3906 4.91211 12.7148 4.91211ZM12.7148 8.95508C13.3789 8.95508 13.9062 8.41797 13.9062 7.76367C13.9062 7.09961 13.3789 6.58203 12.7148 6.58203C12.0508 6.58203 11.5332 7.09961 11.5332 7.76367C11.5332 8.41797 12.0508 8.95508 12.7148 8.95508Z", fillAlpha = 0.85f)
        }
        return _sFKeyCircleFill!!
    }

private var _sFKeyCircleFill: ImageVector? = null
