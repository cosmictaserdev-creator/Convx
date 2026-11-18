package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowshapeUpFill (monochrome)
 * Viewport: 23.1738 x 26.8652
 */
public val SfSymbols.Monochrome.SFArrowshapeUpFill: ImageVector
    get() {
        if (_sFArrowshapeUpFill != null) {
            return _sFArrowshapeUpFill!!
        }
        _sFArrowshapeUpFill = sfIcon(
            name = "Monochrome.SFArrowshapeUpFill",
            viewportWidth = 23.1738f,
            viewportHeight = 26.8652f
        ) {
            addSfPath("M16.2109 24.0625L16.2109 8.54492C16.2109 6.78711 15.2441 5.78125 13.5156 5.78125L9.24805 5.78125C7.5293 5.78125 6.55273 6.78711 6.55273 8.54492L6.55273 24.0625C6.55273 25.8203 7.5293 26.8262 9.24805 26.8262L13.5156 26.8262C15.2441 26.8262 16.2109 25.8203 16.2109 24.0625ZM1.34766 13.457L21.4062 13.457C22.1777 13.457 22.8125 12.9199 22.8125 12.1191C22.8125 11.5527 22.5586 11.1621 22.0117 10.5859L12.7441 0.664062C12.2559 0.136719 11.8066 0 11.4062 0C11.0059 0 10.5566 0.146484 10.0781 0.664062L0.732422 10.5859C0.244141 11.1133 0 11.5723 0 12.1387C0 12.9199 0.576172 13.457 1.34766 13.457Z", fillAlpha = 0.85f)
        }
        return _sFArrowshapeUpFill!!
    }

private var _sFArrowshapeUpFill: ImageVector? = null
