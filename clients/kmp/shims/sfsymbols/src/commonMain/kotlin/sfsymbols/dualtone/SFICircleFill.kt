package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFICircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFICircleFill: ImageVector
    get() {
        if (_sFICircleFill != null) {
            return _sFICircleFill!!
        }
        _sFICircleFill = sfIcon(
            name = "Dualtone.SFICircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M12.7051 18.6133C12.1387 18.6133 11.8262 18.2031 11.8262 17.6172L11.8262 7.65625C11.8262 7.06055 12.1387 6.65039 12.7051 6.65039C13.291 6.65039 13.6035 7.03125 13.6035 7.65625L13.6035 17.6172C13.6035 18.2324 13.291 18.6133 12.7051 18.6133Z", fillAlpha = 0.85f)
        }
        return _sFICircleFill!!
    }

private var _sFICircleFill: ImageVector? = null
