package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFKCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFKCircleFill: ImageVector
    get() {
        if (_sFKCircleFill != null) {
            return _sFKCircleFill!!
        }
        _sFKCircleFill = sfIcon(
            name = "Monochrome.SFKCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM15.5957 7.01172L10.3125 12.373L10.2441 12.373L10.2441 7.65625C10.2441 7.02148 9.92188 6.65039 9.3457 6.65039C8.79883 6.65039 8.4668 7.02148 8.4668 7.65625L8.4668 17.6172C8.4668 18.2422 8.79883 18.6133 9.3457 18.6133C9.92188 18.6133 10.2441 18.2422 10.2441 17.6172L10.2441 14.668L11.8652 13.0078L15.791 18.1543C16.0352 18.4668 16.2695 18.6035 16.5918 18.6035C17.0996 18.6035 17.4609 18.2617 17.4609 17.7637C17.4609 17.5293 17.3438 17.2949 17.1973 17.0703L13.1641 11.8457L16.8652 8.05664C17.0703 7.85156 17.1484 7.66602 17.1484 7.42188C17.1484 6.99219 16.7871 6.65039 16.3281 6.65039C16.0449 6.65039 15.8594 6.75781 15.5957 7.01172Z", fillAlpha = 0.85f)
        }
        return _sFKCircleFill!!
    }

private var _sFKCircleFill: ImageVector? = null
