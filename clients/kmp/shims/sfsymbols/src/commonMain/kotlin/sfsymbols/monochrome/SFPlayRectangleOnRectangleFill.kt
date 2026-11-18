package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPlayRectangleOnRectangleFill (monochrome)
 * Viewport: 33.8086 x 28.2129
 */
public val SfSymbols.Monochrome.SFPlayRectangleOnRectangleFill: ImageVector
    get() {
        if (_sFPlayRectangleOnRectangleFill != null) {
            return _sFPlayRectangleOnRectangleFill!!
        }
        _sFPlayRectangleOnRectangleFill = sfIcon(
            name = "Monochrome.SFPlayRectangleOnRectangleFill",
            viewportWidth = 33.8086f,
            viewportHeight = 28.2129f
        ) {
            addSfPath("M26.1914 5.32227L26.1914 5.625L11.0547 5.625C7.67578 5.625 5.70312 7.57812 5.70312 10.9375L5.70313 21.0352L5 21.0352C2.4707 21.0352 1.19141 19.7754 1.19141 17.2754L1.19141 5.32227C1.19141 2.83203 2.4707 1.5625 5 1.5625L22.3926 1.5625C24.9121 1.5625 26.1914 2.8418 26.1914 5.32227Z", fillAlpha = 0.85f)
            addSfPath("M32.2559 10.9375L32.2559 22.8906C32.2559 25.3711 30.9668 26.6504 28.457 26.6504L11.0547 26.6504C8.53516 26.6504 7.25586 25.3906 7.25586 22.8906L7.25586 10.9375C7.25586 8.44727 8.53516 7.17773 11.0547 7.17773L28.457 7.17773C30.9668 7.17773 32.2559 8.45703 32.2559 10.9375ZM16.1328 12.2949L16.1328 21.543C16.1328 22.0898 16.7188 22.3047 17.168 22.041L24.9219 17.4707C25.3613 17.207 25.3516 16.6113 24.9121 16.3574L17.168 11.7969C16.7188 11.5234 16.1328 11.7383 16.1328 12.2949Z", fillAlpha = 0.85f)
        }
        return _sFPlayRectangleOnRectangleFill!!
    }

private var _sFPlayRectangleOnRectangleFill: ImageVector? = null
