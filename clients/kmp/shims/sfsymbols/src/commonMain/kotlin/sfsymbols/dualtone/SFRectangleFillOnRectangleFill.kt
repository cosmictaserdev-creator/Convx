package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRectangleFillOnRectangleFill (dualtone)
 * Viewport: 33.8086 x 28.2129
 */
public val SfSymbols.Dualtone.SFRectangleFillOnRectangleFill: ImageVector
    get() {
        if (_sFRectangleFillOnRectangleFill != null) {
            return _sFRectangleFillOnRectangleFill!!
        }
        _sFRectangleFillOnRectangleFill = sfIcon(
            name = "Dualtone.SFRectangleFillOnRectangleFill",
            viewportWidth = 33.8086f,
            viewportHeight = 28.2129f
        ) {
            addSfPath("M26.1914 5.32227L26.1914 5.625L11.0547 5.625C7.67578 5.625 5.70312 7.57812 5.70312 10.9375L5.70313 21.0352L5 21.0352C2.4707 21.0352 1.19141 19.7754 1.19141 17.2754L1.19141 5.32227C1.19141 2.83203 2.4707 1.5625 5 1.5625L22.3926 1.5625C24.9121 1.5625 26.1914 2.8418 26.1914 5.32227Z", fillAlpha = 0.425f)
            addSfPath("M11.0547 26.6504L28.457 26.6504C30.9668 26.6504 32.2559 25.3711 32.2559 22.8906L32.2559 10.9375C32.2559 8.45703 30.9668 7.17773 28.457 7.17773L11.0547 7.17773C8.53516 7.17773 7.25586 8.44727 7.25586 10.9375L7.25586 22.8906C7.25586 25.3906 8.53516 26.6504 11.0547 26.6504Z", fillAlpha = 0.85f)
        }
        return _sFRectangleFillOnRectangleFill!!
    }

private var _sFRectangleFillOnRectangleFill: ImageVector? = null
