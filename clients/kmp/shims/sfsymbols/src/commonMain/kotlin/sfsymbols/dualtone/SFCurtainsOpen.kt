package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCurtainsOpen (dualtone)
 * Viewport: 31.2988 x 27.1387
 */
public val SfSymbols.Dualtone.SFCurtainsOpen: ImageVector
    get() {
        if (_sFCurtainsOpen != null) {
            return _sFCurtainsOpen!!
        }
        _sFCurtainsOpen = sfIcon(
            name = "Dualtone.SFCurtainsOpen",
            viewportWidth = 31.2988f,
            viewportHeight = 27.1387f
        ) {
            addSfPath("M3.32031 1.10352L4.4043 1.10352L4.4043 2.8418L3.32031 2.8418C3.03711 3.38867 2.44141 3.75977 1.77734 3.75977C0.800781 3.75977 0 2.95898 0 1.97266C0 0.996094 0.800781 0.185547 1.77734 0.185547C2.44141 0.185547 3.03711 0.566406 3.32031 1.10352ZM30.9375 1.97266C30.9375 2.95898 30.127 3.75977 29.1504 3.75977C28.4863 3.75977 27.9004 3.38867 27.6074 2.8418L26.5332 2.8418L26.5332 1.10352L27.6074 1.10352C27.9004 0.566406 28.4863 0.185547 29.1504 0.185547C30.127 0.185547 30.9375 0.996094 30.9375 1.97266ZM22.6855 2.8418L8.24219 2.8418L8.24219 1.10352L22.6855 1.10352Z", fillAlpha = 0.425f)
            addSfPath("M5.24414 26.3867L7.41211 26.3867C7.89062 26.3867 8.24219 26.0352 8.24219 25.5273L8.24219 0.859375C8.24219 0.361328 7.89062 0 7.41211 0L5.24414 0C4.75586 0 4.4043 0.361328 4.4043 0.859375L4.4043 25.5273C4.4043 26.0352 4.75586 26.3867 5.24414 26.3867ZM23.5254 26.3867L25.6934 26.3867C26.1816 26.3867 26.5332 26.0352 26.5332 25.5273L26.5332 0.859375C26.5332 0.361328 26.1816 0 25.6934 0L23.5254 0C23.0371 0 22.6855 0.361328 22.6855 0.859375L22.6855 25.5273C22.6855 26.0352 23.0371 26.3867 23.5254 26.3867Z", fillAlpha = 0.85f)
        }
        return _sFCurtainsOpen!!
    }

private var _sFCurtainsOpen: ImageVector? = null
