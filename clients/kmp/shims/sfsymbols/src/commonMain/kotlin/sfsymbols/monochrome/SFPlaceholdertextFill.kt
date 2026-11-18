package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPlaceholdertextFill (monochrome)
 * Viewport: 16.4453 x 23.2129
 */
public val SfSymbols.Monochrome.SFPlaceholdertextFill: ImageVector
    get() {
        if (_sFPlaceholdertextFill != null) {
            return _sFPlaceholdertextFill!!
        }
        _sFPlaceholdertextFill = sfIcon(
            name = "Monochrome.SFPlaceholdertextFill",
            viewportWidth = 16.4453f,
            viewportHeight = 23.2129f
        ) {
            addSfPath("M4.72656 0C1.70898 0 0 1.5918 0 4.61914L0 18.5645C0 21.6016 1.70898 23.2129 4.72656 23.2129L11.3867 23.2129C14.4141 23.2129 16.084 21.6016 16.084 18.5645L16.084 4.61914C16.084 1.5918 14.4141 0 11.3867 0Z", fillAlpha = 0.85f)
        }
        return _sFPlaceholdertextFill!!
    }

private var _sFPlaceholdertextFill: ImageVector? = null
