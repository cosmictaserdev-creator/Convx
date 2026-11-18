package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFVisionProFill (monochrome)
 * Viewport: 35.5762 x 20.1465
 */
public val SfSymbols.Monochrome.SFVisionProFill: ImageVector
    get() {
        if (_sFVisionProFill != null) {
            return _sFVisionProFill!!
        }
        _sFVisionProFill = sfIcon(
            name = "Monochrome.SFVisionProFill",
            viewportWidth = 35.5762f,
            viewportHeight = 20.1465f
        ) {
            addSfPath("M17.6074 15.5762C20.0098 15.5762 22.4609 19.5898 27.1875 19.5898C31.7676 19.5898 35.2148 15.4297 35.2148 9.95117C35.2148 0.986328 27.3438 0 17.6074 0C7.87109 0 0 0.996094 0 9.95117C0 15.4297 3.44727 19.5898 8.02734 19.5898C12.7539 19.5898 15.2051 15.5762 17.6074 15.5762Z", fillAlpha = 0.85f)
        }
        return _sFVisionProFill!!
    }

private var _sFVisionProFill: ImageVector? = null
