package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLocationNorthCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFLocationNorthCircleFill: ImageVector
    get() {
        if (_sFLocationNorthCircleFill != null) {
            return _sFLocationNorthCircleFill!!
        }
        _sFLocationNorthCircleFill = sfIcon(
            name = "Monochrome.SFLocationNorthCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7344C25.4395 19.7461 19.7266 25.459 12.7148 25.459C5.71289 25.459 0 19.7461 0 12.7344C0 5.73242 5.71289 0.0195312 12.7148 0.0195312C19.7266 0.0195312 25.4395 5.73242 25.4395 12.7344ZM11.8945 5.71289L6.95312 18.4082C6.65039 19.1602 7.48047 19.7949 8.16406 19.1016L12.4121 14.8535C12.5977 14.668 12.832 14.668 13.0176 14.8535L17.2656 19.1016C17.9492 19.7949 18.7793 19.1602 18.4766 18.4082L13.5254 5.69336C13.2227 4.92188 12.207 4.94141 11.8945 5.71289Z", fillAlpha = 0.85f)
        }
        return _sFLocationNorthCircleFill!!
    }

private var _sFLocationNorthCircleFill: ImageVector? = null
