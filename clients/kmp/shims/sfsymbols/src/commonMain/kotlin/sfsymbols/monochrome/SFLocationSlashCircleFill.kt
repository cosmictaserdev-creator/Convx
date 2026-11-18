package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLocationSlashCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFLocationSlashCircleFill: ImageVector
    get() {
        if (_sFLocationSlashCircleFill != null) {
            return _sFLocationSlashCircleFill!!
        }
        _sFLocationSlashCircleFill = sfIcon(
            name = "Monochrome.SFLocationSlashCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM5.24414 12.2363C4.49219 12.5977 4.61914 13.6719 5.61523 13.6719L11.2793 13.6816C11.4844 13.6816 11.6211 13.8184 11.6211 14.0332L11.6309 19.6289C11.6309 20.6641 12.7148 20.752 13.0566 20.0195L14.6565 16.6194L8.65784 10.6207ZM6.2793 6.2793C6.07422 6.49414 6.08398 6.83594 6.2793 7.03125L18.4082 19.1602C18.6035 19.3555 18.9551 19.3652 19.1602 19.1602C19.3652 18.9355 19.3652 18.6133 19.1602 18.4082L7.04102 6.2793C6.83594 6.07422 6.50391 6.07422 6.2793 6.2793ZM17.666 6.35742L11.3291 9.35648L15.9158 13.9432L18.9062 7.58789C19.3262 6.67969 18.5645 5.9375 17.666 6.35742Z", fillAlpha = 0.85f)
        }
        return _sFLocationSlashCircleFill!!
    }

private var _sFLocationSlashCircleFill: ImageVector? = null
