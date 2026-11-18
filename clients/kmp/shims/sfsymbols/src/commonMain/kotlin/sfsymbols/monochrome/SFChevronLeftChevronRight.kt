package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFChevronLeftChevronRight (monochrome)
 * Viewport: 30.0098 x 21.3574
 */
public val SfSymbols.Monochrome.SFChevronLeftChevronRight: ImageVector
    get() {
        if (_sFChevronLeftChevronRight != null) {
            return _sFChevronLeftChevronRight!!
        }
        _sFChevronLeftChevronRight = sfIcon(
            name = "Monochrome.SFChevronLeftChevronRight",
            viewportWidth = 30.0098f,
            viewportHeight = 21.3574f
        ) {
            addSfPath("M10.3613 21.0352C10.5469 21.2207 10.7812 21.3281 11.0547 21.3281C11.6113 21.3281 12.0312 20.9082 12.0312 20.3613C12.0312 20.0879 11.9238 19.8438 11.748 19.668L1.76758 10.0684L1.76758 11.2598L11.748 1.66992C11.9238 1.48438 12.0312 1.23047 12.0312 0.976562C12.0312 0.419922 11.6113 0 11.0547 0C10.7812 0 10.5566 0.107422 10.3711 0.292969L0.341797 9.92188C0.126953 10.1172 0 10.3906 0 10.6738C0 10.957 0.117188 11.2012 0.332031 11.4062ZM19.2969 21.0352L29.3262 11.4062C29.5312 11.2012 29.6484 10.957 29.6484 10.6738C29.6484 10.3906 29.5312 10.1172 29.3164 9.92188L19.2871 0.292969C19.1016 0.107422 18.8672 0 18.5938 0C18.0469 0 17.627 0.419922 17.627 0.976562C17.627 1.23047 17.7246 1.48438 17.9004 1.66992L27.8809 11.2598L27.8809 10.0684L17.9004 19.668C17.7246 19.8438 17.627 20.0879 17.627 20.3613C17.627 20.9082 18.0469 21.3281 18.5938 21.3281C18.8672 21.3281 19.1016 21.2207 19.2969 21.0352Z", fillAlpha = 0.85f)
        }
        return _sFChevronLeftChevronRight!!
    }

private var _sFChevronLeftChevronRight: ImageVector? = null
