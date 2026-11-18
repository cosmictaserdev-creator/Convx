package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCheckmarkShieldFill (dualtone)
 * Viewport: 20.7324 x 25.3418
 */
public val SfSymbols.Dualtone.SFCheckmarkShieldFill: ImageVector
    get() {
        if (_sFCheckmarkShieldFill != null) {
            return _sFCheckmarkShieldFill!!
        }
        _sFCheckmarkShieldFill = sfIcon(
            name = "Dualtone.SFCheckmarkShieldFill",
            viewportWidth = 20.7324f,
            viewportHeight = 25.3418f
        ) {
            addSfPath("M10.1855 25.3418C10.4102 25.3418 10.7129 25.2441 11.0156 25.0879C18.1152 21.2402 20.3711 19.502 20.3711 15.1367L20.3711 5.63477C20.3711 4.31641 19.8535 3.88672 18.7402 3.42773C17.0703 2.76367 12.8613 1.20117 11.1914 0.634766C10.8594 0.527344 10.5273 0.449219 10.1855 0.449219C9.84375 0.449219 9.51172 0.537109 9.17969 0.634766C7.50977 1.20117 3.30078 2.77344 1.63086 3.42773C0.517578 3.86719 0 4.31641 0 5.63477L0 15.1367C0 19.502 2.37305 21.0449 9.35547 25.0879C9.64844 25.2539 9.96094 25.3418 10.1855 25.3418Z", fillAlpha = 0.2125f)
            addSfPath("M8.79883 18.5059C8.42773 18.5059 8.13477 18.3594 7.85156 17.9883L4.52148 13.9746C4.36523 13.7598 4.26758 13.5059 4.26758 13.2617C4.26758 12.7734 4.63867 12.3828 5.12695 12.3828C5.43945 12.3828 5.68359 12.4902 5.9375 12.832L8.75 16.3477L14.3945 7.36328C14.5996 7.04102 14.873 6.875 15.166 6.875C15.6348 6.875 16.0742 7.20703 16.0742 7.69531C16.0742 7.92969 15.9375 8.18359 15.8105 8.39844L9.69727 17.9883C9.46289 18.3398 9.16016 18.5059 8.79883 18.5059Z", fillAlpha = 0.85f)
        }
        return _sFCheckmarkShieldFill!!
    }

private var _sFCheckmarkShieldFill: ImageVector? = null
