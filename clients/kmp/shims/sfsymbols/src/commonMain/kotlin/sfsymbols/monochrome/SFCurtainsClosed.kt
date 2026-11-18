package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCurtainsClosed (monochrome)
 * Viewport: 31.2988 x 27.1387
 */
public val SfSymbols.Monochrome.SFCurtainsClosed: ImageVector
    get() {
        if (_sFCurtainsClosed != null) {
            return _sFCurtainsClosed!!
        }
        _sFCurtainsClosed = sfIcon(
            name = "Monochrome.SFCurtainsClosed",
            viewportWidth = 31.2988f,
            viewportHeight = 27.1387f
        ) {
            addSfPath("M3.32031 1.10352L4.4043 1.10352L4.4043 2.8418L3.32031 2.8418C3.03711 3.38867 2.44141 3.75977 1.77734 3.75977C0.800781 3.75977 0 2.95898 0 1.97266C0 0.996094 0.800781 0.185547 1.77734 0.185547C2.44141 0.185547 3.03711 0.566406 3.32031 1.10352ZM30.9375 1.97266C30.9375 2.95898 30.127 3.75977 29.1504 3.75977C28.4863 3.75977 27.9004 3.38867 27.6074 2.8418L26.5332 2.8418L26.5332 1.10352L27.6074 1.10352C27.9004 0.566406 28.4863 0.185547 29.1504 0.185547C30.127 0.185547 30.9375 0.996094 30.9375 1.97266ZM16.25 2.8418L14.6875 2.8418L14.6875 1.10352L16.25 1.10352Z", fillAlpha = 0.85f)
            addSfPath("M5.24414 26.3867L13.8574 26.3867C14.3359 26.3867 14.6875 26.0352 14.6875 25.5273L14.6875 0.859375C14.6875 0.361328 14.3359 0 13.8574 0L5.24414 0C4.75586 0 4.4043 0.361328 4.4043 0.859375L4.4043 25.5273C4.4043 26.0352 4.75586 26.3867 5.24414 26.3867ZM17.0801 26.3867L25.6934 26.3867C26.1816 26.3867 26.5332 26.0352 26.5332 25.5273L26.5332 0.859375C26.5332 0.361328 26.1816 0 25.6934 0L17.0801 0C16.5918 0 16.25 0.361328 16.25 0.859375L16.25 25.5273C16.25 26.0352 16.5918 26.3867 17.0801 26.3867Z", fillAlpha = 0.85f)
        }
        return _sFCurtainsClosed!!
    }

private var _sFCurtainsClosed: ImageVector? = null
