package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFOpticaldiscdriveFill (monochrome)
 * Viewport: 29.1699 x 20.8496
 */
public val SfSymbols.Monochrome.SFOpticaldiscdriveFill: ImageVector
    get() {
        if (_sFOpticaldiscdriveFill != null) {
            return _sFOpticaldiscdriveFill!!
        }
        _sFOpticaldiscdriveFill = sfIcon(
            name = "Monochrome.SFOpticaldiscdriveFill",
            viewportWidth = 29.1699f,
            viewportHeight = 20.8496f
        ) {
            addSfPath("M4.69727 15.957C4.69727 15.5273 5.04883 15.166 5.49805 15.166L23.3105 15.166C23.75 15.166 24.1016 15.5273 24.1016 15.957C24.1016 16.3965 23.75 16.7578 23.3105 16.7578L5.49805 16.7578C5.04883 16.7578 4.69727 16.3965 4.69727 15.957ZM4.92188 20.8496L23.8867 20.8496C26.7578 20.8496 28.8086 18.8086 28.8086 15.957C28.8086 13.1152 26.7578 11.0645 23.8867 11.0645L4.92188 11.0645C2.05078 11.0645 0 13.1152 0 15.957C0 18.8086 2.05078 20.8496 4.92188 20.8496ZM4.92188 9.50195L23.8867 9.50195C25.0488 9.50195 26.1035 9.77539 27.0215 10.2539L23.8379 2.80273C23.0762 0.986328 21.5332 0.0195312 19.4727 0.0195312L9.33594 0.0195312C7.27539 0.0195312 5.72266 0.986328 4.9707 2.80273L1.77734 10.2539C2.69531 9.77539 3.75977 9.50195 4.92188 9.50195Z", fillAlpha = 0.85f)
        }
        return _sFOpticaldiscdriveFill!!
    }

private var _sFOpticaldiscdriveFill: ImageVector? = null
