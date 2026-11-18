package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFChevronLeft2 (monochrome)
 * Viewport: 23.6523 x 21.3574
 */
public val SfSymbols.Monochrome.SFChevronLeft2: ImageVector
    get() {
        if (_sFChevronLeft2 != null) {
            return _sFChevronLeft2!!
        }
        _sFChevronLeft2 = sfIcon(
            name = "Monochrome.SFChevronLeft2",
            viewportWidth = 23.6523f,
            viewportHeight = 21.3574f
        ) {
            addSfPath("M21.6211 21.0352C21.8066 21.2207 22.041 21.3281 22.3145 21.3281C22.8711 21.3281 23.291 20.9082 23.291 20.3613C23.291 20.0879 23.1836 19.8438 23.0078 19.668L13.0273 10.0684L13.0273 11.2598L23.0078 1.66992C23.1836 1.48438 23.291 1.23047 23.291 0.976562C23.291 0.419922 22.8711 0 22.3145 0C22.041 0 21.8164 0.107422 21.6211 0.292969L11.5918 9.92188C11.3867 10.1172 11.2598 10.3906 11.2598 10.6738C11.2598 10.957 11.377 11.2012 11.5918 11.4062Z", fillAlpha = 0.85f)
            addSfPath("M10.3613 21.0352C10.5469 21.2207 10.7812 21.3281 11.0547 21.3281C11.6113 21.3281 12.0312 20.9082 12.0312 20.3613C12.0312 20.0879 11.9238 19.8438 11.748 19.668L1.76758 10.0684L1.76758 11.2598L11.748 1.66992C11.9238 1.48438 12.0312 1.23047 12.0312 0.976562C12.0312 0.419922 11.6113 0 11.0547 0C10.7812 0 10.5566 0.107422 10.3711 0.292969L0.341797 9.92188C0.126953 10.1172 0 10.3906 0 10.6738C0 10.957 0.117188 11.2012 0.332031 11.4062Z", fillAlpha = 0.85f)
        }
        return _sFChevronLeft2!!
    }

private var _sFChevronLeft2: ImageVector? = null
