package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFVideoSlashCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFVideoSlashCircleFill: ImageVector
    get() {
        if (_sFVideoSlashCircleFill != null) {
            return _sFVideoSlashCircleFill!!
        }
        _sFVideoSlashCircleFill = sfIcon(
            name = "Monochrome.SFVideoSlashCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7344C25.4395 19.7461 19.7266 25.459 12.7148 25.459C5.71289 25.459 0 19.7461 0 12.7344C0 5.73242 5.71289 0.0195312 12.7148 0.0195312C19.7266 0.0195312 25.4395 5.73242 25.4395 12.7344ZM6.2793 6.29883C6.07422 6.51367 6.08398 6.85547 6.2793 7.05078L18.4082 19.1699C18.6035 19.375 18.9551 19.375 19.1602 19.1699C19.3652 18.9453 19.3652 18.623 19.1602 18.418L7.04102 6.29883C6.83594 6.08398 6.50391 6.08398 6.2793 6.29883ZM5.37109 9.60938L5.37109 15.8691C5.37109 17.1582 6.13281 17.8906 7.42188 17.8906L14.2383 17.8906C14.7707 17.8906 15.2116 17.7657 15.5439 17.5263L6.03786 8.02028C5.61232 8.36206 5.37109 8.90188 5.37109 9.60938ZM19.4531 8.88672L16.9922 10.9668L16.9922 14.5117L19.4531 16.582C19.6777 16.7773 19.9219 16.8848 20.1465 16.8848C20.6152 16.8848 20.9277 16.5527 20.9277 16.0352L20.9277 9.44336C20.9277 8.92578 20.6152 8.59375 20.1465 8.59375C19.9219 8.59375 19.6582 8.71094 19.4531 8.88672ZM9.54925 7.58789L16.2793 14.3234L16.2793 9.60938C16.2793 8.32031 15.5859 7.58789 14.2871 7.58789Z", fillAlpha = 0.85f)
        }
        return _sFVideoSlashCircleFill!!
    }

private var _sFVideoSlashCircleFill: ImageVector? = null
