package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowshapeUpCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFArrowshapeUpCircleFill: ImageVector
    get() {
        if (_sFArrowshapeUpCircleFill != null) {
            return _sFArrowshapeUpCircleFill!!
        }
        _sFArrowshapeUpCircleFill = sfIcon(
            name = "Dualtone.SFArrowshapeUpCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M15.4688 18.4277L15.4688 9.86328C15.4688 8.85742 14.9121 8.30078 13.9453 8.30078L11.5137 8.30078C10.5566 8.30078 10 8.85742 10 9.86328L10 18.4277C10 19.4336 10.5566 19.9902 11.5137 19.9902L13.9453 19.9902C14.9121 19.9902 15.4688 19.4336 15.4688 18.4277ZM7.02148 12.4121L18.3984 12.4121C18.8281 12.4121 19.1895 12.1094 19.1895 11.6406C19.1895 11.3379 19.0527 11.123 18.7402 10.8008L13.4961 5.15625C13.2031 4.86328 12.959 4.78516 12.7246 4.78516C12.5 4.78516 12.2559 4.86328 11.9727 5.15625L6.66992 10.8008C6.40625 11.0938 6.25977 11.3477 6.25977 11.6602C6.25977 12.1094 6.57227 12.4121 7.02148 12.4121Z", fillAlpha = 0.85f)
        }
        return _sFArrowshapeUpCircleFill!!
    }

private var _sFArrowshapeUpCircleFill: ImageVector? = null
