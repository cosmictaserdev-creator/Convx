package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRecordCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFRecordCircleFill: ImageVector
    get() {
        if (_sFRecordCircleFill != null) {
            return _sFRecordCircleFill!!
        }
        _sFRecordCircleFill = sfIcon(
            name = "Dualtone.SFRecordCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M12.7148 17.7051C9.95117 17.7051 7.70508 15.459 7.70508 12.6953C7.70508 9.92188 9.95117 7.68555 12.7148 7.68555C15.4883 7.68555 17.7246 9.92188 17.7246 12.6953C17.7246 15.459 15.4883 17.7051 12.7148 17.7051Z", fillAlpha = 0.85f)
        }
        return _sFRecordCircleFill!!
    }

private var _sFRecordCircleFill: ImageVector? = null
