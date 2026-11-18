package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFKCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFKCircleFill: ImageVector
    get() {
        if (_sFKCircleFill != null) {
            return _sFKCircleFill!!
        }
        _sFKCircleFill = sfIcon(
            name = "Dualtone.SFKCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M9.3457 18.6133C8.79883 18.6133 8.4668 18.2422 8.4668 17.6172L8.4668 7.65625C8.4668 7.02148 8.79883 6.65039 9.3457 6.65039C9.92188 6.65039 10.2441 7.02148 10.2441 7.65625L10.2441 12.373L10.3125 12.373L15.5957 7.01172C15.8594 6.75781 16.0449 6.65039 16.3281 6.65039C16.7871 6.65039 17.1484 6.99219 17.1484 7.42188C17.1484 7.66602 17.0703 7.85156 16.8652 8.05664L13.1641 11.8457L17.1973 17.0703C17.3438 17.2949 17.4609 17.5293 17.4609 17.7637C17.4609 18.2617 17.0996 18.6035 16.5918 18.6035C16.2695 18.6035 16.0352 18.4668 15.791 18.1543L11.8652 13.0078L10.2441 14.668L10.2441 17.6172C10.2441 18.2422 9.92188 18.6133 9.3457 18.6133Z", fillAlpha = 0.85f)
        }
        return _sFKCircleFill!!
    }

private var _sFKCircleFill: ImageVector? = null
