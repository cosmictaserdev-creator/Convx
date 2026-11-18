package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SF7CircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SF7CircleFill: ImageVector
    get() {
        if (_sF7CircleFill != null) {
            return _sF7CircleFill!!
        }
        _sF7CircleFill = sfIcon(
            name = "Dualtone.SF7CircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M10.9863 18.7793C10.4883 18.7793 10.0977 18.4473 10.0977 17.9688C10.0977 17.7441 10.1465 17.5879 10.2539 17.3828L15.0781 8.39844L15.0781 8.30078L9.44336 8.30078C9.05273 8.30078 8.76953 8.00781 8.76953 7.60742C8.76953 7.1875 9.04297 6.89453 9.44336 6.89453L15.7715 6.89453C16.5137 6.89453 17.0117 7.36328 17.0117 7.98828C17.0117 8.24219 16.9629 8.4668 16.7383 8.88672L11.8652 18.1641C11.6406 18.6035 11.377 18.7793 10.9863 18.7793Z", fillAlpha = 0.85f)
        }
        return _sF7CircleFill!!
    }

private var _sF7CircleFill: ImageVector? = null
