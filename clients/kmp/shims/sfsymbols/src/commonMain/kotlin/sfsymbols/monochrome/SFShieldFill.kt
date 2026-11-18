package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFShieldFill (monochrome)
 * Viewport: 20.7324 x 25.3418
 */
public val SfSymbols.Monochrome.SFShieldFill: ImageVector
    get() {
        if (_sFShieldFill != null) {
            return _sFShieldFill!!
        }
        _sFShieldFill = sfIcon(
            name = "Monochrome.SFShieldFill",
            viewportWidth = 20.7324f,
            viewportHeight = 25.3418f
        ) {
            addSfPath("M10.1855 25.3418C10.4102 25.3418 10.7129 25.2441 11.0156 25.0879C18.1152 21.2402 20.3711 19.502 20.3711 15.1367L20.3711 5.63477C20.3711 4.31641 19.8535 3.88672 18.7402 3.42773C17.0703 2.76367 12.8613 1.20117 11.1914 0.634766C10.8594 0.527344 10.5273 0.449219 10.1855 0.449219C9.84375 0.449219 9.51172 0.537109 9.17969 0.634766C7.50977 1.20117 3.30078 2.77344 1.63086 3.42773C0.517578 3.86719 0 4.31641 0 5.63477L0 15.1367C0 19.502 2.37305 21.0449 9.35547 25.0879C9.64844 25.2539 9.96094 25.3418 10.1855 25.3418Z", fillAlpha = 0.85f)
        }
        return _sFShieldFill!!
    }

private var _sFShieldFill: ImageVector? = null
