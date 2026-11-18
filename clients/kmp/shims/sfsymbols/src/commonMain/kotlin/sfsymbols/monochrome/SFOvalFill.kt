package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFOvalFill (monochrome)
 * Viewport: 32.168 x 24.0137
 */
public val SfSymbols.Monochrome.SFOvalFill: ImageVector
    get() {
        if (_sFOvalFill != null) {
            return _sFOvalFill!!
        }
        _sFOvalFill = sfIcon(
            name = "Monochrome.SFOvalFill",
            viewportWidth = 32.168f,
            viewportHeight = 24.0137f
        ) {
            addSfPath("M0 12.002C0 18.9746 6.5918 24.0137 15.9082 24.0137C25.2148 24.0137 31.8066 18.9746 31.8066 12.002C31.8066 5.0293 25.2148 0 15.9082 0C6.5918 0 0 5.0293 0 12.002Z", fillAlpha = 0.85f)
        }
        return _sFOvalFill!!
    }

private var _sFOvalFill: ImageVector? = null
