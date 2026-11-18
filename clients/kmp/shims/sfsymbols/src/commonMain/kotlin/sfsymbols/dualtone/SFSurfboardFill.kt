package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSurfboardFill (dualtone)
 * Viewport: 30.875 x 31.9918
 */
public val SfSymbols.Dualtone.SFSurfboardFill: ImageVector
    get() {
        if (_sFSurfboardFill != null) {
            return _sFSurfboardFill!!
        }
        _sFSurfboardFill = sfIcon(
            name = "Dualtone.SFSurfboardFill",
            viewportWidth = 30.875f,
            viewportHeight = 31.9918f
        ) {
            addSfPath("M10.4082 10.7664C5.79881 15.3856 2.24412 20.2977 0.593732 24.5848C-0.324237 26.9871-0.177752 28.8035 1.03319 30.2586L28.6894 2.60236C25.1445-0.327329 18.7187 2.45587 10.4082 10.7664ZM29.3828 3.30548L1.72654 30.9617C3.18162 32.1629 5.00779 32.3192 7.40037 31.4012C11.6875 29.7508 16.6094 26.1961 21.2187 21.5867C29.539 13.2664 32.3222 6.85041 29.3828 3.30548Z", fillAlpha = 0.85f)
        }
        return _sFSurfboardFill!!
    }

private var _sFSurfboardFill: ImageVector? = null
