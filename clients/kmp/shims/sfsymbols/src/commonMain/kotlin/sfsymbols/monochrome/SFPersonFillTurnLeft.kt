package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPersonFillTurnLeft (monochrome)
 * Viewport: 22.8906 x 21.2793
 */
public val SfSymbols.Monochrome.SFPersonFillTurnLeft: ImageVector
    get() {
        if (_sFPersonFillTurnLeft != null) {
            return _sFPersonFillTurnLeft!!
        }
        _sFPersonFillTurnLeft = sfIcon(
            name = "Monochrome.SFPersonFillTurnLeft",
            viewportWidth = 22.8906f,
            viewportHeight = 21.2793f
        ) {
            addSfPath("M22.5293 2.50977C22.5293 0.703125 22.002 0 20.9082 0C17.666 0 13.1934 4.05273 13.1934 10.5664C13.1934 17.0801 17.666 21.1328 20.9082 21.1328C22.002 21.1328 22.5293 20.4297 22.5293 18.623ZM10.9375 10.5762C10.9375 7.86133 8.52539 5.57617 5.41016 5.57617C2.37305 5.56641 0 7.88086 0 10.5762C0 13.2812 2.33398 15.5859 5.39062 15.5859C8.52539 15.5859 10.9375 13.3008 10.9375 10.5762Z", fillAlpha = 0.85f)
        }
        return _sFPersonFillTurnLeft!!
    }

private var _sFPersonFillTurnLeft: ImageVector? = null
