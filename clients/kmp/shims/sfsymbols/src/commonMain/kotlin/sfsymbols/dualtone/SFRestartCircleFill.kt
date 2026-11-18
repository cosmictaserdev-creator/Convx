package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRestartCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFRestartCircleFill: ImageVector
    get() {
        if (_sFRestartCircleFill != null) {
            return _sFRestartCircleFill!!
        }
        _sFRestartCircleFill = sfIcon(
            name = "Dualtone.SFRestartCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.459C19.7266 25.459 25.4395 19.7461 25.4395 12.7344C25.4395 5.73242 19.7266 0.0195312 12.7148 0.0195312C5.71289 0.0195312 0 5.73242 0 12.7344C0 19.7461 5.71289 25.459 12.7148 25.459Z", fillAlpha = 0.2125f)
            addSfPath("M6.92383 11.4941L14.5801 6.91406C15.4785 6.38672 16.709 6.75781 16.709 7.99805L16.709 17.4512C16.709 18.6914 15.4785 19.0625 14.5801 18.5352L6.9043 13.9844C5.98633 13.418 5.9668 12.0801 6.92383 11.4941ZM8.58398 12.5977C8.47656 12.666 8.45703 12.8125 8.59375 12.8906L14.6387 16.4844C14.8047 16.582 14.9219 16.5137 14.9219 16.377L14.9219 9.07227C14.9219 8.93555 14.8047 8.86719 14.6387 8.97461Z", fillAlpha = 0.85f)
        }
        return _sFRestartCircleFill!!
    }

private var _sFRestartCircleFill: ImageVector? = null
