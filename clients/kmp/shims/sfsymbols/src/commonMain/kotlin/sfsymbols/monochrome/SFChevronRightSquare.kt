package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFChevronRightSquare (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFChevronRightSquare: ImageVector
    get() {
        if (_sFChevronRightSquare != null) {
            return _sFChevronRightSquare!!
        }
        _sFChevronRightSquare = sfIcon(
            name = "Monochrome.SFChevronRightSquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.85f)
            addSfPath("M8.67188 17.6758C8.96484 17.959 9.52148 17.9492 9.83398 17.6465L15.2051 12.5293C15.8203 11.9629 15.8203 10.9961 15.2051 10.4199L9.83398 5.30273C9.49219 4.98047 8.99414 4.9707 8.68164 5.27344C8.34961 5.58594 8.33984 6.12305 8.67188 6.42578L13.9746 11.4648L8.67188 16.5137C8.34961 16.8262 8.34961 17.3535 8.67188 17.6758Z", fillAlpha = 0.85f)
        }
        return _sFChevronRightSquare!!
    }

private var _sFChevronRightSquare: ImageVector? = null
