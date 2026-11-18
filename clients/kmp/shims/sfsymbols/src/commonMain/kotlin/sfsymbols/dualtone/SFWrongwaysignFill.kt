package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFWrongwaysignFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFWrongwaysignFill: ImageVector
    get() {
        if (_sFWrongwaysignFill != null) {
            return _sFWrongwaysignFill!!
        }
        _sFWrongwaysignFill = sfIcon(
            name = "Dualtone.SFWrongwaysignFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.459C19.7266 25.459 25.4395 19.7461 25.4395 12.7344C25.4395 5.73242 19.7266 0.0195312 12.7148 0.0195312C5.71289 0.0195312 0 5.73242 0 12.7344C0 19.7461 5.71289 25.459 12.7148 25.459Z", fillAlpha = 0.2125f)
            addSfPath("M6.16211 15.6738C5.51758 15.6738 5.03906 15.2051 5.03906 14.5508L5.03906 10.8887C5.03906 10.2344 5.51758 9.76562 6.16211 9.76562L19.2871 9.76562C19.9414 9.76562 20.4199 10.2344 20.4199 10.8887L20.4199 14.5312C20.4199 15.1953 19.9414 15.6738 19.2871 15.6738Z", fillAlpha = 0.85f)
        }
        return _sFWrongwaysignFill!!
    }

private var _sFWrongwaysignFill: ImageVector? = null
