package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFChevronLeftSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFChevronLeftSquareFill: ImageVector
    get() {
        if (_sFChevronLeftSquareFill != null) {
            return _sFChevronLeftSquareFill!!
        }
        _sFChevronLeftSquareFill = sfIcon(
            name = "Dualtone.SFChevronLeftSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M13.9355 17.6758C13.6426 17.959 13.0859 17.9492 12.7832 17.6465L7.40234 12.5293C6.78711 11.9629 6.78711 10.9961 7.40234 10.4199L12.7832 5.30273C13.1152 4.98047 13.6133 4.9707 13.9258 5.27344C14.2578 5.58594 14.2676 6.12305 13.9355 6.42578L8.63281 11.4648L13.9355 16.5137C14.2578 16.8262 14.2676 17.3535 13.9355 17.6758Z", fillAlpha = 0.85f)
        }
        return _sFChevronLeftSquareFill!!
    }

private var _sFChevronLeftSquareFill: ImageVector? = null
