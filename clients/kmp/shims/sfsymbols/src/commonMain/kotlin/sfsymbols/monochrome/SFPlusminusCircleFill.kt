package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPlusminusCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFPlusminusCircleFill: ImageVector
    get() {
        if (_sFPlusminusCircleFill != null) {
            return _sFPlusminusCircleFill!!
        }
        _sFPlusminusCircleFill = sfIcon(
            name = "Monochrome.SFPlusminusCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7344C25.4395 19.7461 19.7266 25.459 12.7148 25.459C5.71289 25.459 0 19.7461 0 12.7344C0 5.73242 5.71289 0.0195312 12.7148 0.0195312C19.7266 0.0195312 25.4395 5.73242 25.4395 12.7344ZM9.0918 17.2266C8.66211 17.2266 8.31055 17.5879 8.31055 18.0078C8.31055 18.3984 8.66211 18.7891 9.0918 18.7891L16.3574 18.7891C16.7969 18.7891 17.1484 18.3984 17.1484 18.0078C17.1484 17.5879 16.7969 17.2266 16.3574 17.2266ZM11.9531 6.5625L11.9531 9.42383L9.08203 9.42383C8.66211 9.42383 8.31055 9.78516 8.31055 10.2051C8.31055 10.6348 8.66211 10.9961 9.08203 10.9961L11.9531 10.9961L11.9531 13.8574C11.9531 14.2676 12.3145 14.6289 12.7344 14.6289C13.1641 14.6289 13.5254 14.2773 13.5254 13.8574L13.5254 10.9961L16.3672 10.9961C16.7969 10.9961 17.1484 10.6348 17.1484 10.2051C17.1484 9.78516 16.7969 9.42383 16.3672 9.42383L13.5254 9.42383L13.5254 6.5625C13.5254 6.13281 13.1543 5.78125 12.7344 5.78125C12.3145 5.79102 11.9531 6.14258 11.9531 6.5625Z", fillAlpha = 0.85f)
        }
        return _sFPlusminusCircleFill!!
    }

private var _sFPlusminusCircleFill: ImageVector? = null
