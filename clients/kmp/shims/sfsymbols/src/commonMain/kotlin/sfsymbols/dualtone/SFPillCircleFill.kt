package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPillCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFPillCircleFill: ImageVector
    get() {
        if (_sFPillCircleFill != null) {
            return _sFPillCircleFill!!
        }
        _sFPillCircleFill = sfIcon(
            name = "Dualtone.SFPillCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.459C19.7266 25.459 25.4395 19.7461 25.4395 12.7344C25.4395 5.73242 19.7266 0.0195312 12.7148 0.0195312C5.71289 0.0195312 0 5.73242 0 12.7344C0 19.7461 5.71289 25.459 12.7148 25.459Z", fillAlpha = 0.2125f)
            addSfPath("M18.3203 7.13867C19.8145 8.58398 19.7949 10.5566 18.2617 12.0898L15.5078 14.8438L10.5957 9.93164L13.3496 7.1875C14.873 5.6543 16.8164 5.66406 18.3203 7.13867ZM7.09961 18.3594C5.625 16.8945 5.64453 14.9219 7.16797 13.3887L9.92188 10.625L14.8145 15.5273L12.0703 18.2812C10.5566 19.7949 8.58398 19.8145 7.09961 18.3594Z", fillAlpha = 0.85f)
        }
        return _sFPillCircleFill!!
    }

private var _sFPillCircleFill: ImageVector? = null
