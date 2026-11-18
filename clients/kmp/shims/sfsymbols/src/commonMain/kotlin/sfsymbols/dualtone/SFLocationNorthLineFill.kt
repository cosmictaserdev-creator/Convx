package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLocationNorthLineFill (dualtone)
 * Viewport: 17.4023 x 32.5684
 */
public val SfSymbols.Dualtone.SFLocationNorthLineFill: ImageVector
    get() {
        if (_sFLocationNorthLineFill != null) {
            return _sFLocationNorthLineFill!!
        }
        _sFLocationNorthLineFill = sfIcon(
            name = "Dualtone.SFLocationNorthLineFill",
            viewportWidth = 17.4023f,
            viewportHeight = 32.5684f
        ) {
            addSfPath("M1.31836 30.8301C1.82617 30.8301 2.33398 30.5469 2.77344 30.1465L8.22266 25.0488C8.33984 24.9316 8.42773 24.8633 8.51562 24.8633C8.60352 24.8633 8.69141 24.9316 8.81836 25.0488L14.2676 30.1465C14.6973 30.5469 15.2148 30.8301 15.7227 30.8301C16.4746 30.8301 17.041 30.1367 17.041 29.4043C17.041 28.9844 16.875 28.5156 16.6504 27.9395L10.0488 10.9863C9.66797 10.0293 9.16992 9.60938 8.51562 9.60938C7.87109 9.60938 7.36328 10.0293 6.99219 10.9863L0.390625 27.9395C0.166016 28.5156 0 28.9844 0 29.4043C0 30.1367 0.566406 30.8301 1.31836 30.8301ZM8.51562 7.63672C9.02344 7.63672 9.38477 7.28516 9.38477 6.69922L9.38477 0.9375C9.38477 0.351562 9.02344 0 8.51562 0C8.01758 0 7.65625 0.351562 7.65625 0.9375L7.65625 6.69922C7.65625 7.28516 8.01758 7.63672 8.51562 7.63672Z", fillAlpha = 0.85f)
        }
        return _sFLocationNorthLineFill!!
    }

private var _sFLocationNorthLineFill: ImageVector? = null
