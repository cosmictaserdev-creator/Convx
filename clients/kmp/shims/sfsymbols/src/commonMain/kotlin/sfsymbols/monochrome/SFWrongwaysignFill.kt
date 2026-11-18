package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFWrongwaysignFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFWrongwaysignFill: ImageVector
    get() {
        if (_sFWrongwaysignFill != null) {
            return _sFWrongwaysignFill!!
        }
        _sFWrongwaysignFill = sfIcon(
            name = "Monochrome.SFWrongwaysignFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7344C25.4395 19.7461 19.7266 25.459 12.7148 25.459C5.71289 25.459 0 19.7461 0 12.7344C0 5.73242 5.71289 0.0195312 12.7148 0.0195312C19.7266 0.0195312 25.4395 5.73242 25.4395 12.7344ZM6.16211 9.76562C5.51758 9.76562 5.03906 10.2344 5.03906 10.8887L5.03906 14.5508C5.03906 15.2051 5.51758 15.6738 6.16211 15.6738L19.2871 15.6738C19.9414 15.6738 20.4199 15.1953 20.4199 14.5312L20.4199 10.8887C20.4199 10.2344 19.9414 9.76562 19.2871 9.76562Z", fillAlpha = 0.85f)
        }
        return _sFWrongwaysignFill!!
    }

private var _sFWrongwaysignFill: ImageVector? = null
