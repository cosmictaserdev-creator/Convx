package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFChevronLeftSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFChevronLeftSquareFill: ImageVector
    get() {
        if (_sFChevronLeftSquareFill != null) {
            return _sFChevronLeftSquareFill!!
        }
        _sFChevronLeftSquareFill = sfIcon(
            name = "Monochrome.SFChevronLeftSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM12.7832 5.30273L7.40234 10.4199C6.78711 10.9961 6.78711 11.9629 7.40234 12.5293L12.7832 17.6465C13.0859 17.9492 13.6426 17.959 13.9355 17.6758C14.2676 17.3535 14.2578 16.8262 13.9355 16.5137L8.63281 11.4648L13.9355 6.42578C14.2676 6.12305 14.2578 5.58594 13.9258 5.27344C13.6133 4.9707 13.1152 4.98047 12.7832 5.30273Z", fillAlpha = 0.85f)
        }
        return _sFChevronLeftSquareFill!!
    }

private var _sFChevronLeftSquareFill: ImageVector? = null
