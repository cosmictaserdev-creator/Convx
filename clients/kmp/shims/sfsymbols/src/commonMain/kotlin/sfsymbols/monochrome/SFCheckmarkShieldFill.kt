package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCheckmarkShieldFill (monochrome)
 * Viewport: 20.7324 x 25.3418
 */
public val SfSymbols.Monochrome.SFCheckmarkShieldFill: ImageVector
    get() {
        if (_sFCheckmarkShieldFill != null) {
            return _sFCheckmarkShieldFill!!
        }
        _sFCheckmarkShieldFill = sfIcon(
            name = "Monochrome.SFCheckmarkShieldFill",
            viewportWidth = 20.7324f,
            viewportHeight = 25.3418f
        ) {
            addSfPath("M11.1914 0.634766C12.8613 1.20117 17.0703 2.76367 18.7402 3.42773C19.8535 3.88672 20.3711 4.31641 20.3711 5.63477L20.3711 15.1367C20.3711 19.502 18.1152 21.2402 11.0156 25.0879C10.7129 25.2441 10.4102 25.3418 10.1855 25.3418C9.96094 25.3418 9.64844 25.2539 9.35547 25.0879C2.37305 21.0449 0 19.502 0 15.1367L0 5.63477C0 4.31641 0.517578 3.86719 1.63086 3.42773C3.30078 2.77344 7.50977 1.20117 9.17969 0.634766C9.51172 0.537109 9.84375 0.449219 10.1855 0.449219C10.5273 0.449219 10.8594 0.527344 11.1914 0.634766ZM14.3945 7.36328L8.75 16.3477L5.9375 12.832C5.68359 12.4902 5.43945 12.3828 5.12695 12.3828C4.63867 12.3828 4.26758 12.7734 4.26758 13.2617C4.26758 13.5059 4.36523 13.7598 4.52148 13.9746L7.85156 17.9883C8.13477 18.3594 8.42773 18.5059 8.79883 18.5059C9.16016 18.5059 9.46289 18.3398 9.69727 17.9883L15.8105 8.39844C15.9375 8.18359 16.0742 7.92969 16.0742 7.69531C16.0742 7.20703 15.6348 6.875 15.166 6.875C14.873 6.875 14.5996 7.04102 14.3945 7.36328Z", fillAlpha = 0.85f)
        }
        return _sFCheckmarkShieldFill!!
    }

private var _sFCheckmarkShieldFill: ImageVector? = null
