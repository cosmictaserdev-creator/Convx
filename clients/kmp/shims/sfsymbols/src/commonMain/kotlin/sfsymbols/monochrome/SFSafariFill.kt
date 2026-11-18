package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSafariFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFSafariFill: ImageVector
    get() {
        if (_sFSafariFill != null) {
            return _sFSafariFill!!
        }
        _sFSafariFill = sfIcon(
            name = "Monochrome.SFSafariFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM18.2422 6.18164L10.752 9.90234C10.3809 10.0684 10.1172 10.332 9.90234 10.752L6.17188 18.2617C5.79102 19.0527 6.38672 19.668 7.1875 19.2773L14.6973 15.5469C15.0879 15.3613 15.3613 15.0781 15.5566 14.6973L19.2676 7.19727C19.6582 6.38672 19.0918 5.77148 18.2422 6.18164ZM14.3164 12.7344C14.3164 13.6133 13.6035 14.3164 12.7246 14.3164C11.8555 14.3164 11.1426 13.6133 11.1426 12.7344C11.1426 11.8652 11.8555 11.1523 12.7246 11.1523C13.6035 11.1523 14.3164 11.8652 14.3164 12.7344Z", fillAlpha = 0.85f)
        }
        return _sFSafariFill!!
    }

private var _sFSafariFill: ImageVector? = null
