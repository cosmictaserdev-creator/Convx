package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMinusCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFMinusCircleFill: ImageVector
    get() {
        if (_sFMinusCircleFill != null) {
            return _sFMinusCircleFill!!
        }
        _sFMinusCircleFill = sfIcon(
            name = "Monochrome.SFMinusCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM7.44141 11.8066C6.80664 11.8066 6.39648 12.1582 6.39648 12.7539C6.39648 13.3301 6.82617 13.6719 7.44141 13.6719L17.998 13.6719C18.6133 13.6719 19.043 13.3301 19.043 12.7539C19.043 12.1582 18.6426 11.8066 17.998 11.8066Z", fillAlpha = 0.85f)
        }
        return _sFMinusCircleFill!!
    }

private var _sFMinusCircleFill: ImageVector? = null
