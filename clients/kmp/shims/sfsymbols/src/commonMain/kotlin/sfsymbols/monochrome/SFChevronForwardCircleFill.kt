package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFChevronForwardCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFChevronForwardCircleFill: ImageVector
    get() {
        if (_sFChevronForwardCircleFill != null) {
            return _sFChevronForwardCircleFill!!
        }
        _sFChevronForwardCircleFill = sfIcon(
            name = "Monochrome.SFChevronForwardCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM9.82422 6.37695C9.48242 6.70898 9.47266 7.25586 9.81445 7.57812L15.2441 12.7344L9.81445 17.9004C9.48242 18.2129 9.47266 18.75 9.81445 19.0918C10.1074 19.375 10.6934 19.3652 11.0059 19.0527L16.5137 13.8281C17.1484 13.2422 17.1484 12.2363 16.5137 11.6406L11.0059 6.41602C10.6641 6.08398 10.1465 6.07422 9.82422 6.37695Z", fillAlpha = 0.85f)
        }
        return _sFChevronForwardCircleFill!!
    }

private var _sFChevronForwardCircleFill: ImageVector? = null
