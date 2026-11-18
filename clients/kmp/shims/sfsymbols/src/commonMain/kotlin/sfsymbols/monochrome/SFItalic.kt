package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFItalic (monochrome)
 * Viewport: 13.291 x 18.0762
 */
public val SfSymbols.Monochrome.SFItalic: ImageVector
    get() {
        if (_sFItalic != null) {
            return _sFItalic!!
        }
        _sFItalic = sfIcon(
            name = "Monochrome.SFItalic",
            viewportWidth = 13.291f,
            viewportHeight = 18.0762f
        ) {
            addSfPath("M0.859375 18.0566L8.88672 18.0566C9.39453 18.0566 9.74609 17.7441 9.74609 17.2363C9.74609 16.748 9.4043 16.4355 8.89648 16.4355L5.85938 16.4355L9.04297 1.62109L12.0703 1.62109C12.5781 1.62109 12.9297 1.30859 12.9297 0.800781C12.9297 0.3125 12.5879 0 12.0801 0L4.0332 0C3.52539 0 3.18359 0.3125 3.18359 0.800781C3.18359 1.30859 3.54492 1.62109 4.04297 1.62109L7.07031 1.62109L3.88672 16.4355L0.849609 16.4355C0.341797 16.4355 0 16.748 0 17.2363C0 17.7441 0.351562 18.0566 0.859375 18.0566Z", fillAlpha = 0.85f)
        }
        return _sFItalic!!
    }

private var _sFItalic: ImageVector? = null
