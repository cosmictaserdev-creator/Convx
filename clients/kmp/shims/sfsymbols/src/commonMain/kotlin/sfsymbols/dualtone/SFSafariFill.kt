package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSafariFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFSafariFill: ImageVector
    get() {
        if (_sFSafariFill != null) {
            return _sFSafariFill!!
        }
        _sFSafariFill = sfIcon(
            name = "Dualtone.SFSafariFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M7.1875 19.2773C6.38672 19.668 5.79102 19.0527 6.17188 18.2617L9.90234 10.752C10.1172 10.332 10.3809 10.0684 10.752 9.90234L18.2422 6.18164C19.0918 5.77148 19.6582 6.38672 19.2676 7.19727L15.5566 14.6973C15.3613 15.0781 15.0879 15.3613 14.6973 15.5469ZM12.7246 14.3164C13.6035 14.3164 14.3164 13.6133 14.3164 12.7344C14.3164 11.8652 13.6035 11.1523 12.7246 11.1523C11.8555 11.1523 11.1426 11.8652 11.1426 12.7344C11.1426 13.6133 11.8555 14.3164 12.7246 14.3164Z", fillAlpha = 0.85f)
        }
        return _sFSafariFill!!
    }

private var _sFSafariFill: ImageVector? = null
