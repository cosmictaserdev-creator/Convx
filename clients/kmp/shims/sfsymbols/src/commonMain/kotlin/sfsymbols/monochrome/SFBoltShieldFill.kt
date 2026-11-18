package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBoltShieldFill (monochrome)
 * Viewport: 20.7324 x 25.3418
 */
public val SfSymbols.Monochrome.SFBoltShieldFill: ImageVector
    get() {
        if (_sFBoltShieldFill != null) {
            return _sFBoltShieldFill!!
        }
        _sFBoltShieldFill = sfIcon(
            name = "Monochrome.SFBoltShieldFill",
            viewportWidth = 20.7324f,
            viewportHeight = 25.3418f
        ) {
            addSfPath("M11.1914 0.634766C12.8613 1.20117 17.0703 2.76367 18.7402 3.42773C19.8535 3.88672 20.3711 4.31641 20.3711 5.63477L20.3711 15.1367C20.3711 19.502 18.1152 21.2402 11.0156 25.0879C10.7129 25.2441 10.4102 25.3418 10.1855 25.3418C9.96094 25.3418 9.64844 25.2539 9.35547 25.0879C2.37305 21.0449 0 19.502 0 15.1367L0 5.63477C0 4.31641 0.517578 3.86719 1.63086 3.42773C3.30078 2.77344 7.50977 1.20117 9.17969 0.634766C9.51172 0.537109 9.84375 0.449219 10.1855 0.449219C10.5273 0.449219 10.8594 0.527344 11.1914 0.634766ZM11.8945 3.80859L4.70703 12.8418C4.57031 13.0078 4.50195 13.1738 4.50195 13.3203C4.50195 13.623 4.73633 13.8379 5.03906 13.8379L9.50195 13.8379L7.10938 20.2539C6.81641 21.0254 7.63672 21.4355 8.13477 20.8203L15.332 11.7871C15.4688 11.6211 15.5371 11.4551 15.5371 11.3086C15.5371 11.0059 15.3027 10.791 14.9902 10.791L10.5371 10.791L12.9297 4.375C13.2227 3.61328 12.4023 3.19336 11.8945 3.80859Z", fillAlpha = 0.85f)
        }
        return _sFBoltShieldFill!!
    }

private var _sFBoltShieldFill: ImageVector? = null
