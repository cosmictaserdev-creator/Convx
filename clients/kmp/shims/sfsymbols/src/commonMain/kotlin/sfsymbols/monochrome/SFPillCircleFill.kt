package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPillCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFPillCircleFill: ImageVector
    get() {
        if (_sFPillCircleFill != null) {
            return _sFPillCircleFill!!
        }
        _sFPillCircleFill = sfIcon(
            name = "Monochrome.SFPillCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7344C25.4395 19.7461 19.7266 25.459 12.7148 25.459C5.71289 25.459 0 19.7461 0 12.7344C0 5.73242 5.71289 0.0195312 12.7148 0.0195312C19.7266 0.0195312 25.4395 5.73242 25.4395 12.7344ZM7.16797 13.3887C5.64453 14.9219 5.625 16.8945 7.09961 18.3594C8.58398 19.8145 10.5566 19.7949 12.0703 18.2812L14.8145 15.5273L9.92188 10.625ZM13.3496 7.1875L10.5957 9.93164L15.5078 14.8438L18.2617 12.0898C19.7949 10.5566 19.8145 8.58398 18.3203 7.13867C16.8164 5.66406 14.873 5.6543 13.3496 7.1875Z", fillAlpha = 0.85f)
        }
        return _sFPillCircleFill!!
    }

private var _sFPillCircleFill: ImageVector? = null
