package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPersonFillTurnRight (dualtone)
 * Viewport: 22.8906 x 21.2793
 */
public val SfSymbols.Dualtone.SFPersonFillTurnRight: ImageVector
    get() {
        if (_sFPersonFillTurnRight != null) {
            return _sFPersonFillTurnRight!!
        }
        _sFPersonFillTurnRight = sfIcon(
            name = "Dualtone.SFPersonFillTurnRight",
            viewportWidth = 22.8906f,
            viewportHeight = 21.2793f
        ) {
            addSfPath("M0 2.50977L0 18.623C0 20.4297 0.517578 21.1328 1.62109 21.1328C4.86328 21.1328 9.33594 17.0801 9.33594 10.5664C9.33594 4.05273 4.86328 0 1.62109 0C0.517578 0 0 0.703125 0 2.50977ZM11.5918 10.5762C11.5918 13.3008 14.0039 15.5859 17.1387 15.5859C20.1953 15.5859 22.5293 13.2812 22.5293 10.5762C22.5293 7.88086 20.1562 5.56641 17.1191 5.57617C14.0039 5.57617 11.5918 7.86133 11.5918 10.5762Z", fillAlpha = 0.85f)
        }
        return _sFPersonFillTurnRight!!
    }

private var _sFPersonFillTurnRight: ImageVector? = null
