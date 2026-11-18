package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPencilTipCropCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFPencilTipCropCircleFill: ImageVector
    get() {
        if (_sFPencilTipCropCircleFill != null) {
            return _sFPencilTipCropCircleFill!!
        }
        _sFPencilTipCropCircleFill = sfIcon(
            name = "Dualtone.SFPencilTipCropCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M10.0098 14.7949L8.51562 21.7285C8.33008 22.6367 6.91406 22.4219 7.13867 21.3867L8.65234 14.3555C8.76953 13.8184 9.20898 13.4082 9.73633 13.3789L9.88281 13.3789L11.9922 6.99219C12.2363 6.25977 13.2129 6.26953 13.457 6.99219L15.5566 13.3789L15.7129 13.3789C16.2402 13.4082 16.6895 13.8184 16.7969 14.3652L18.3203 21.3867C18.5352 22.4219 17.1289 22.6367 16.9434 21.7285L15.4395 14.7949Z", fillAlpha = 0.85f)
        }
        return _sFPencilTipCropCircleFill!!
    }

private var _sFPencilTipCropCircleFill: ImageVector? = null
