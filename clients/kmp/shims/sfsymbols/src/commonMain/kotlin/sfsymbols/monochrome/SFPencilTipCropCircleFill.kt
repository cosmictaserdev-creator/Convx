package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPencilTipCropCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFPencilTipCropCircleFill: ImageVector
    get() {
        if (_sFPencilTipCropCircleFill != null) {
            return _sFPencilTipCropCircleFill!!
        }
        _sFPencilTipCropCircleFill = sfIcon(
            name = "Monochrome.SFPencilTipCropCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM11.9922 6.99219L9.88281 13.3789L9.73633 13.3789C9.20898 13.4082 8.76953 13.8184 8.65234 14.3555L7.13867 21.3867C6.91406 22.4219 8.33008 22.6367 8.51562 21.7285L10.0098 14.7949L15.4395 14.7949L16.9434 21.7285C17.1289 22.6367 18.5352 22.4219 18.3203 21.3867L16.7969 14.3652C16.6895 13.8184 16.2402 13.4082 15.7129 13.3789L15.5566 13.3789L13.457 6.99219C13.2129 6.26953 12.2363 6.25977 11.9922 6.99219Z", fillAlpha = 0.85f)
        }
        return _sFPencilTipCropCircleFill!!
    }

private var _sFPencilTipCropCircleFill: ImageVector? = null
