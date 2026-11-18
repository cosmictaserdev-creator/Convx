package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCrossCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFCrossCircleFill: ImageVector
    get() {
        if (_sFCrossCircleFill != null) {
            return _sFCrossCircleFill!!
        }
        _sFCrossCircleFill = sfIcon(
            name = "Monochrome.SFCrossCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7344C25.4395 19.7461 19.7266 25.459 12.7148 25.459C5.71289 25.459 0 19.7461 0 12.7344C0 5.73242 5.71289 0.0195312 12.7148 0.0195312C19.7266 0.0195312 25.4395 5.73242 25.4395 12.7344ZM11.3867 5.69336C10.5957 5.69336 10.0684 6.2207 10.0684 7.02148L10.0684 10.0684L6.99219 10.0684C6.21094 10.0684 5.68359 10.6055 5.68359 11.3867L5.68359 14.043C5.68359 14.8242 6.2207 15.3613 6.99219 15.3613L10.0684 15.3613L10.0684 18.4277C10.0684 19.2285 10.5957 19.7559 11.3867 19.7559L14.0332 19.7559C14.8242 19.7559 15.3516 19.2285 15.3516 18.4277L15.3516 15.3613L18.4277 15.3613C19.209 15.3613 19.7461 14.8242 19.7461 14.043L19.7461 11.3867C19.7461 10.6055 19.209 10.0684 18.4277 10.0684L15.3516 10.0684L15.3516 7.02148C15.3516 6.2207 14.8242 5.69336 14.0332 5.69336Z", fillAlpha = 0.85f)
        }
        return _sFCrossCircleFill!!
    }

private var _sFCrossCircleFill: ImageVector? = null
