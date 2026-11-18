package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPersonFill (monochrome)
 * Viewport: 21.4941 x 22.5293
 */
public val SfSymbols.Monochrome.SFPersonFill: ImageVector
    get() {
        if (_sFPersonFill != null) {
            return _sFPersonFill!!
        }
        _sFPersonFill = sfIcon(
            name = "Monochrome.SFPersonFill",
            viewportWidth = 21.4941f,
            viewportHeight = 22.5293f
        ) {
            addSfPath("M2.06055 22.5195L19.0723 22.5195C20.3516 22.5195 21.1328 21.9043 21.1328 20.9082C21.1328 17.666 17.0801 13.1934 10.5664 13.1934C4.05273 13.1934 0 17.666 0 20.9082C0 21.9043 0.78125 22.5195 2.06055 22.5195ZM10.5762 10.9375C13.3008 10.9375 15.5859 8.51562 15.5859 5.39062C15.5859 2.33398 13.2812 0 10.5762 0C7.88086 0 5.57617 2.37305 5.57617 5.41016C5.57617 8.51562 7.86133 10.9375 10.5762 10.9375Z", fillAlpha = 0.85f)
        }
        return _sFPersonFill!!
    }

private var _sFPersonFill: ImageVector? = null
