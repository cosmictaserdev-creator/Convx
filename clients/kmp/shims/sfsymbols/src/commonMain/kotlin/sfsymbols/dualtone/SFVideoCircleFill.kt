package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFVideoCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFVideoCircleFill: ImageVector
    get() {
        if (_sFVideoCircleFill != null) {
            return _sFVideoCircleFill!!
        }
        _sFVideoCircleFill = sfIcon(
            name = "Dualtone.SFVideoCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.459C19.7266 25.459 25.4395 19.7461 25.4395 12.7344C25.4395 5.73242 19.7266 0.0195312 12.7148 0.0195312C5.71289 0.0195312 0 5.73242 0 12.7344C0 19.7461 5.71289 25.459 12.7148 25.459Z", fillAlpha = 0.2125f)
            addSfPath("M7.42188 17.8906C6.13281 17.8906 5.37109 17.1582 5.37109 15.8691L5.37109 9.60938C5.37109 8.32031 6.17188 7.58789 7.42188 7.58789L14.2871 7.58789C15.5859 7.58789 16.2793 8.32031 16.2793 9.60938L16.2793 15.8691C16.2793 17.1582 15.5273 17.8906 14.2383 17.8906ZM16.9922 14.5117L16.9922 10.9668L19.4531 8.88672C19.6582 8.71094 19.9219 8.59375 20.1465 8.59375C20.6152 8.59375 20.9277 8.92578 20.9277 9.44336L20.9277 16.0352C20.9277 16.5527 20.6152 16.8848 20.1465 16.8848C19.9219 16.8848 19.6777 16.7773 19.4531 16.582Z", fillAlpha = 0.85f)
        }
        return _sFVideoCircleFill!!
    }

private var _sFVideoCircleFill: ImageVector? = null
