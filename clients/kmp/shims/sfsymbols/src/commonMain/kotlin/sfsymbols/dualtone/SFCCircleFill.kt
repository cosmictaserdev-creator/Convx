package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFCCircleFill: ImageVector
    get() {
        if (_sFCCircleFill != null) {
            return _sFCCircleFill!!
        }
        _sFCCircleFill = sfIcon(
            name = "Dualtone.SFCCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M12.7637 18.7402C9.48242 18.7402 7.38281 16.3379 7.38281 12.6367C7.38281 8.93555 9.49219 6.52344 12.7637 6.52344C15.0098 6.52344 16.8457 7.67578 17.4121 9.4043C17.4805 9.63867 17.5 9.76562 17.5 9.95117C17.5 10.4395 17.207 10.7324 16.7285 10.7324C16.377 10.7324 16.1523 10.5566 15.9766 10.1562C15.5176 8.80859 14.3457 8.01758 12.7832 8.01758C10.6348 8.01758 9.17969 9.89258 9.17969 12.6367C9.17969 15.3906 10.6348 17.2461 12.7832 17.2461C14.3457 17.2461 15.5078 16.4648 15.9766 15.1074C16.1523 14.707 16.377 14.5312 16.7285 14.5312C17.207 14.5312 17.5 14.8242 17.5 15.3125C17.5 15.4785 17.4805 15.625 17.4121 15.8594C16.8555 17.6074 15.0293 18.7402 12.7637 18.7402Z", fillAlpha = 0.85f)
        }
        return _sFCCircleFill!!
    }

private var _sFCCircleFill: ImageVector? = null
