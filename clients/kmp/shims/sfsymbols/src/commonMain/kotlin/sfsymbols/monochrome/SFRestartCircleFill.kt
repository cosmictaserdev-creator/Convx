package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRestartCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFRestartCircleFill: ImageVector
    get() {
        if (_sFRestartCircleFill != null) {
            return _sFRestartCircleFill!!
        }
        _sFRestartCircleFill = sfIcon(
            name = "Monochrome.SFRestartCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7344C25.4395 19.7461 19.7266 25.459 12.7148 25.459C5.71289 25.459 0 19.7461 0 12.7344C0 5.73242 5.71289 0.0195312 12.7148 0.0195312C19.7266 0.0195312 25.4395 5.73242 25.4395 12.7344ZM14.5801 6.91406L6.92383 11.4941C5.9668 12.0801 5.98633 13.418 6.9043 13.9844L14.5801 18.5352C15.4785 19.0625 16.709 18.6914 16.709 17.4512L16.709 7.99805C16.709 6.75781 15.4785 6.38672 14.5801 6.91406ZM14.9219 9.07227L14.9219 16.377C14.9219 16.5137 14.8047 16.582 14.6387 16.4844L8.59375 12.8906C8.45703 12.8125 8.47656 12.666 8.58398 12.5977L14.6387 8.97461C14.8047 8.86719 14.9219 8.93555 14.9219 9.07227Z", fillAlpha = 0.85f)
        }
        return _sFRestartCircleFill!!
    }

private var _sFRestartCircleFill: ImageVector? = null
