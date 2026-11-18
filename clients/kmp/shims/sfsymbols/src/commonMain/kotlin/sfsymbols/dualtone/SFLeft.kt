package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLeft (dualtone)
 * Viewport: 11.7676 x 18.6621
 */
public val SfSymbols.Dualtone.SFLeft: ImageVector
    get() {
        if (_sFLeft != null) {
            return _sFLeft!!
        }
        _sFLeft = sfIcon(
            name = "Dualtone.SFLeft",
            viewportWidth = 11.7676f,
            viewportHeight = 18.6621f
        ) {
            addSfPath("M0.966797 18.3594L10.459 18.3594C11.0059 18.3594 11.4062 17.998 11.4062 17.4707C11.4062 16.9531 11.0059 16.582 10.459 16.582L1.92383 16.582L1.92383 1.00586C1.92383 0.419922 1.5332 0 0.966797 0C0.390625 0 0 0.419922 0 1.00586L0 17.3633C0 17.959 0.371094 18.3594 0.966797 18.3594Z", fillAlpha = 0.85f)
        }
        return _sFLeft!!
    }

private var _sFLeft: ImageVector? = null
