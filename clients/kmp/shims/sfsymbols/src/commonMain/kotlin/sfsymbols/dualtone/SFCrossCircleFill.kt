package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCrossCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFCrossCircleFill: ImageVector
    get() {
        if (_sFCrossCircleFill != null) {
            return _sFCrossCircleFill!!
        }
        _sFCrossCircleFill = sfIcon(
            name = "Dualtone.SFCrossCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.459C19.7266 25.459 25.4395 19.7461 25.4395 12.7344C25.4395 5.73242 19.7266 0.0195312 12.7148 0.0195312C5.71289 0.0195312 0 5.73242 0 12.7344C0 19.7461 5.71289 25.459 12.7148 25.459Z", fillAlpha = 0.2125f)
            addSfPath("M5.68359 14.043L5.68359 11.3867C5.68359 10.6055 6.21094 10.0684 6.99219 10.0684L10.0684 10.0684L10.0684 7.02148C10.0684 6.2207 10.5957 5.69336 11.3867 5.69336L14.0332 5.69336C14.8242 5.69336 15.3516 6.2207 15.3516 7.02148L15.3516 10.0684L18.4277 10.0684C19.209 10.0684 19.7461 10.6055 19.7461 11.3867L19.7461 14.043C19.7461 14.8242 19.209 15.3613 18.4277 15.3613L15.3516 15.3613L15.3516 18.4277C15.3516 19.2285 14.8242 19.7559 14.0332 19.7559L11.3867 19.7559C10.5957 19.7559 10.0684 19.2285 10.0684 18.4277L10.0684 15.3613L6.99219 15.3613C6.2207 15.3613 5.68359 14.8242 5.68359 14.043Z", fillAlpha = 0.85f)
        }
        return _sFCrossCircleFill!!
    }

private var _sFCrossCircleFill: ImageVector? = null
