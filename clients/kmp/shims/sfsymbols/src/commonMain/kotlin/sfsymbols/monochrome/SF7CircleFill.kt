package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SF7CircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SF7CircleFill: ImageVector
    get() {
        if (_sF7CircleFill != null) {
            return _sF7CircleFill!!
        }
        _sF7CircleFill = sfIcon(
            name = "Monochrome.SF7CircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM9.44336 6.89453C9.04297 6.89453 8.76953 7.1875 8.76953 7.60742C8.76953 8.00781 9.05273 8.30078 9.44336 8.30078L15.0781 8.30078L15.0781 8.39844L10.2539 17.3828C10.1465 17.5879 10.0977 17.7441 10.0977 17.9688C10.0977 18.4473 10.4883 18.7793 10.9863 18.7793C11.377 18.7793 11.6406 18.6035 11.8652 18.1641L16.7383 8.88672C16.9629 8.4668 17.0117 8.24219 17.0117 7.98828C17.0117 7.36328 16.5137 6.89453 15.7715 6.89453Z", fillAlpha = 0.85f)
        }
        return _sF7CircleFill!!
    }

private var _sF7CircleFill: ImageVector? = null
