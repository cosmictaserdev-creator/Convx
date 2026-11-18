package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMinusCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFMinusCircleFill: ImageVector
    get() {
        if (_sFMinusCircleFill != null) {
            return _sFMinusCircleFill!!
        }
        _sFMinusCircleFill = sfIcon(
            name = "Dualtone.SFMinusCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M7.44141 13.6719C6.82617 13.6719 6.39648 13.3301 6.39648 12.7539C6.39648 12.1582 6.80664 11.8066 7.44141 11.8066L17.998 11.8066C18.6426 11.8066 19.043 12.1582 19.043 12.7539C19.043 13.3301 18.6133 13.6719 17.998 13.6719Z", fillAlpha = 0.85f)
        }
        return _sFMinusCircleFill!!
    }

private var _sFMinusCircleFill: ImageVector? = null
