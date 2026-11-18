package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFChevronRightCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFChevronRightCircleFill: ImageVector
    get() {
        if (_sFChevronRightCircleFill != null) {
            return _sFChevronRightCircleFill!!
        }
        _sFChevronRightCircleFill = sfIcon(
            name = "Dualtone.SFChevronRightCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M9.81445 19.0918C9.47266 18.75 9.48242 18.2129 9.81445 17.9004L15.2441 12.7344L9.81445 7.57812C9.47266 7.25586 9.48242 6.70898 9.82422 6.37695C10.1465 6.07422 10.6641 6.08398 11.0059 6.41602L16.5137 11.6406C17.1484 12.2363 17.1484 13.2422 16.5137 13.8281L11.0059 19.0527C10.6934 19.3652 10.1074 19.375 9.81445 19.0918Z", fillAlpha = 0.85f)
        }
        return _sFChevronRightCircleFill!!
    }

private var _sFChevronRightCircleFill: ImageVector? = null
