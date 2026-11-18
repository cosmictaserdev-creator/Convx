package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLocationNorthCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFLocationNorthCircleFill: ImageVector
    get() {
        if (_sFLocationNorthCircleFill != null) {
            return _sFLocationNorthCircleFill!!
        }
        _sFLocationNorthCircleFill = sfIcon(
            name = "Dualtone.SFLocationNorthCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.459C19.7266 25.459 25.4395 19.7461 25.4395 12.7344C25.4395 5.73242 19.7266 0.0195312 12.7148 0.0195312C5.71289 0.0195312 0 5.73242 0 12.7344C0 19.7461 5.71289 25.459 12.7148 25.459Z", fillAlpha = 0.2125f)
            addSfPath("M6.95312 18.4082L11.8945 5.71289C12.207 4.94141 13.2227 4.92188 13.5254 5.69336L18.4766 18.4082C18.7793 19.1602 17.9492 19.7949 17.2656 19.1016L13.0176 14.8535C12.832 14.668 12.5977 14.668 12.4121 14.8535L8.16406 19.1016C7.48047 19.7949 6.65039 19.1602 6.95312 18.4082Z", fillAlpha = 0.85f)
        }
        return _sFLocationNorthCircleFill!!
    }

private var _sFLocationNorthCircleFill: ImageVector? = null
