package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBoltShieldFill (dualtone)
 * Viewport: 20.7324 x 25.3418
 */
public val SfSymbols.Dualtone.SFBoltShieldFill: ImageVector
    get() {
        if (_sFBoltShieldFill != null) {
            return _sFBoltShieldFill!!
        }
        _sFBoltShieldFill = sfIcon(
            name = "Dualtone.SFBoltShieldFill",
            viewportWidth = 20.7324f,
            viewportHeight = 25.3418f
        ) {
            addSfPath("M10.1855 25.3418C10.4102 25.3418 10.7129 25.2441 11.0156 25.0879C18.1152 21.2402 20.3711 19.502 20.3711 15.1367L20.3711 5.63477C20.3711 4.31641 19.8535 3.88672 18.7402 3.42773C17.0703 2.76367 12.8613 1.20117 11.1914 0.634766C10.8594 0.527344 10.5273 0.449219 10.1855 0.449219C9.84375 0.449219 9.51172 0.537109 9.17969 0.634766C7.50977 1.20117 3.30078 2.77344 1.63086 3.42773C0.517578 3.86719 0 4.31641 0 5.63477L0 15.1367C0 19.502 2.37305 21.0449 9.35547 25.0879C9.64844 25.2539 9.96094 25.3418 10.1855 25.3418Z", fillAlpha = 0.2125f)
            addSfPath("M4.50195 13.3203C4.50195 13.1738 4.57031 13.0078 4.70703 12.8418L11.8945 3.80859C12.4023 3.19336 13.2227 3.61328 12.9297 4.375L10.5371 10.791L14.9902 10.791C15.3027 10.791 15.5371 11.0059 15.5371 11.3086C15.5371 11.4551 15.4688 11.6211 15.332 11.7871L8.13477 20.8203C7.63672 21.4355 6.81641 21.0254 7.10938 20.2539L9.50195 13.8379L5.03906 13.8379C4.73633 13.8379 4.50195 13.623 4.50195 13.3203Z", fillAlpha = 0.85f)
        }
        return _sFBoltShieldFill!!
    }

private var _sFBoltShieldFill: ImageVector? = null
